package ch.scaille.tcwriter.server.webapi.v0.controllers;

import static ch.scaille.tcwriter.server.facade.ValidationHelper.validateDictionarySet;
import static ch.scaille.tcwriter.server.facade.ValidationHelper.validateTestCaseSet;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.scaille.tcwriter.generated.api.controllers.v0.TestcaseApiController;
import ch.scaille.tcwriter.generated.api.model.v0.ExportType;
import ch.scaille.tcwriter.generated.api.model.v0.Metadata;
import ch.scaille.tcwriter.generated.api.model.v0.TestCase;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.server.WebConstants;
import ch.scaille.tcwriter.server.exceptions.WebRTException;
import ch.scaille.tcwriter.server.facade.TestCaseFacade;
import ch.scaille.tcwriter.server.facade.WebFeedbackFacade;
import ch.scaille.tcwriter.server.services.SessionAccessor;
import ch.scaille.tcwriter.server.webapi.v0.mappers.MetadataMapper;
import ch.scaille.tcwriter.server.webapi.v0.mappers.TestCaseMapper;
import jakarta.validation.Valid;

public class TestCaseController extends TestcaseApiController {

	private final TestCaseFacade testCaseFacade;

	private final SessionAccessor sessionAccessor;
	
	private final WebFeedbackFacade webFeedbackFacade;

	public TestCaseController(SessionAccessor sessionAccessor, TestCaseFacade testCaseFacade, WebFeedbackFacade webFeedbackFacade,
			NativeWebRequest request) {
		super(request);
		this.sessionAccessor = sessionAccessor;
		this.testCaseFacade = testCaseFacade;
		this.webFeedbackFacade = webFeedbackFacade;
	}

	@Override
	public ResponseEntity<List<Metadata>> listAll() {
		return ResponseEntity.ok(
				testCaseFacade.listAll(getCurrentDictionaryId()).stream().map(MetadataMapper.MAPPER::convert).toList());
	}

	@Override
	public ResponseEntity<TestCase> testcase(@Valid String tc) {
		final var loadedTC = loadValidTestCase(tc);
		final var dto = TestCaseMapper.MAPPER.convert(loadedTC);
		final var humanReadables = testCaseFacade.computeHumanReadableTexts(loadedTC, loadedTC.getSteps());
		for (int i = 0; i < dto.getSteps().size(); i++) {
			dto.getSteps().get(i).setHumanReadable(humanReadables.get(i));
		}
		return ResponseEntity.ok(dto);
	}

	@Override
	public ResponseEntity<Void> executeTestCase(@Valid String tc, @Valid String tabId) {
		final var loadedTC = loadValidTestCase(tc);
		final var wsSessionId = sessionAccessor.webSocketSessionIdOf(getRequest(), tabId).get();
		testCaseFacade.executeTest(loadedTC, s -> webFeedbackFacade.send(wsSessionId, tabId,
				WebConstants.TEST_EXECUTION_FEEDBACK, TestCaseMapper.MAPPER.convert(s)));
		return ResponseEntity.ok(null);
	}

	@Override
	public ResponseEntity<String> exportTestCase(@Valid String tc, @Valid ExportType format) {
		final var loadedTC = loadValidTestCase(tc);
		if (format == ExportType.JAVA) {
			return exportJava(loadedTC);
		}
		return exportHumanReadable(loadedTC);
	}

	private ResponseEntity<String> exportHumanReadable(ExportableTestCase tc) {
		try {
			final var headers = new org.springframework.http.HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			return new ResponseEntity<>(
					new ObjectMapper().writeValueAsString(testCaseFacade.computeHumanReadableTexts(tc, tc.getSteps())),
					headers, HttpStatus.OK);
		} catch (JsonProcessingException e) {
			throw new WebRTException(e);
		}

	}

	private ResponseEntity<String> exportJava(ExportableTestCase tc) {
		var headers = new org.springframework.http.HttpHeaders();
		headers.setContentType(new MediaType("text", "java"));
		return new ResponseEntity<>(testCaseFacade.generateCode(tc), headers, HttpStatus.OK);
	}

	private ExportableTestCase loadValidTestCase(String tc) {
		final var dictionaryId = getCurrentDictionaryId();
		final String tcId;
		if ("current".equals(tc)) {
			tcId = validateTestCaseSet(sessionAccessor.getContext(getRequest()).mandatory().getTestCase());
		} else {
			tcId = tc;
		}
		return testCaseFacade.load(tcId, dictionaryId);
	}

	private String getCurrentDictionaryId() {
		return validateDictionarySet(sessionAccessor.getContext(getRequest()).mandatory().getDictionary());
	}
}
