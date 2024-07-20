package ch.scaille.tcwriter.server.webapi.v0.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
import jakarta.validation.constraints.NotNull;

public class TestCaseController extends TestcaseApiController {

	private final TestCaseFacade testCaseFacade;

	private final SessionAccessor sessionAccessor;

	private final WebFeedbackFacade webFeedbackFacade;

	public TestCaseController(SessionAccessor sessionAccessor, TestCaseFacade testCaseFacade,
			WebFeedbackFacade webFeedbackFacade, NativeWebRequest request) {
		super(request);
		this.sessionAccessor = sessionAccessor;
		this.testCaseFacade = testCaseFacade;
		this.webFeedbackFacade = webFeedbackFacade;
	}

	@Transactional(readOnly = true)
	@Override
	public ResponseEntity<List<Metadata>> listAll(@Valid @NotNull String dictionary) {
		return ResponseEntity
				.ok(testCaseFacade.listAll(dictionary).stream().map(MetadataMapper.MAPPER::convert).toList());
	}

	@Transactional(readOnly = true)
	@Override
	public ResponseEntity<TestCase> testcase(@Valid @NotNull String tc, @Valid @NotNull String dictionary) {
		final var loadedTC = loadValidTestCase(tc, dictionary);
		final var dto = TestCaseMapper.MAPPER.convert(loadedTC);
		final var humanReadables = testCaseFacade.computeHumanReadableTexts(loadedTC, loadedTC.getSteps());
		for (int i = 0; i < dto.getSteps().size(); i++) {
			dto.getSteps().get(i).setHumanReadable(humanReadables.get(i));
		}
		return ResponseEntity.ok(dto);
	}

	@Transactional()
	@Override
	public ResponseEntity<Void> executeTestCase(@Valid @NotNull String tc, @Valid @NotNull String dictionary,
			@Valid @NotNull String tabId) {
		final var loadedTC = loadValidTestCase(tc, dictionary);
		final var wsSessionId = sessionAccessor.webSocketSessionIdOf(getRequest().orElse(null), tabId).get();
		testCaseFacade.executeTest(loadedTC, s -> webFeedbackFacade.send(wsSessionId.orElse(null), tabId,
				WebConstants.TEST_EXECUTION_FEEDBACK, TestCaseMapper.MAPPER.convert(s)));
		return ResponseEntity.ok(null);
	}

	@Override
	public ResponseEntity<String> exportTestCase(String tc, @NotNull String dictionary, @Valid ExportType format) {
		final var loadedTC = loadValidTestCase(tc, dictionary);
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

	private ExportableTestCase loadValidTestCase(String tc, String dictionary) {
		return testCaseFacade.load(tc, dictionary);
	}

}
