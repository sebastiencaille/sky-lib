package ch.scaille.tcwriter.server.webapi.v0.webservices;

import java.util.List;
import java.util.Optional;

import ch.scaille.tcwriter.generated.api.controllers.v0.TestcaseApi;
import ch.scaille.tcwriter.server.facade.DictionaryFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.scaille.tcwriter.generated.api.model.v0.ExportType;
import ch.scaille.tcwriter.generated.api.model.v0.Metadata;
import ch.scaille.tcwriter.generated.api.model.v0.TestCase;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.server.WebConstants;
import ch.scaille.tcwriter.server.exceptions.WebRTException;
import ch.scaille.tcwriter.server.facade.TestCaseFacade;
import ch.scaille.tcwriter.server.facade.WebFeedbackFacade;
import ch.scaille.tcwriter.server.services.SessionManager;
import ch.scaille.tcwriter.server.webapi.v0.mappers.MetadataMapper;
import ch.scaille.tcwriter.server.webapi.v0.mappers.TestCaseMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("${openapi.testCaseServer.base-path:rest/v0}")
public class TestCaseWebService implements TestcaseApi {

	private final TestCaseFacade testCaseFacade;

	private final DictionaryFacade dictionaryFacade;

	private final SessionManager sessionAccessor;

	private final WebFeedbackFacade webFeedbackFacade;
	private final NativeWebRequest request;

	public TestCaseWebService(SessionManager sessionAccessor,
							  TestCaseFacade testCaseFacade,
							  WebFeedbackFacade webFeedbackFacade, NativeWebRequest request,
							  DictionaryFacade dictionaryFacade) {
		this.request = request;
		this.sessionAccessor = sessionAccessor;
		this.testCaseFacade = testCaseFacade;
		this.webFeedbackFacade = webFeedbackFacade;
        this.dictionaryFacade = dictionaryFacade;
    }

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return Optional.ofNullable(request);
	}

	@Transactional(readOnly = true)
	@Override
	public ResponseEntity<List<Metadata>> listAllTestCases(@Valid @NotNull String dictionary) {
		return ResponseEntity
				.ok(testCaseFacade.listAll(dictionary).stream().map(MetadataMapper.MAPPER::convert).toList());
	}

	@Transactional(readOnly = true)
	@Override
	public ResponseEntity<TestCase> testcase(@Valid @NotNull String tc, @Valid @NotNull String dictionary) {
		final var loadedTC = loadValidTestCase(tc, dictionary);
		final var dto = TestCaseMapper.MAPPER.convertToDto(loadedTC);
		final var humanReadables = testCaseFacade.computeHumanReadableTexts(loadedTC, loadedTC.getSteps());
		for (int i = 0; i < dto.getSteps().size(); i++) {
			dto.getSteps().get(i).setHumanReadable(humanReadables.get(i));
		}
		return ResponseEntity.ok(dto);
	}

	@Transactional
	@Override
	public ResponseEntity<Void> createTestcase(@Valid String dictionary, @Valid TestCase testCase) {
		testCaseFacade.saveTestCase(TestCaseMapper.MAPPER.convertToExportable(testCase, dictionaryFacade.load(dictionary)));
		return ResponseEntity.ok().build();
	}

	@Transactional()
	@Override
	public ResponseEntity<Void> executeTestCase(@Valid @NotNull String tc,
			@Valid @NotNull String tabId, @Valid @NotNull String dictionary) {
		final var loadedTC = loadValidTestCase(tc, dictionary);
		final var wsSessionId = sessionAccessor.webSocketSessionIdOf(getRequest().orElse(null), tabId).get();
		testCaseFacade.executeTest(loadedTC, s -> webFeedbackFacade.send(wsSessionId.orElse(null), tabId,
				WebConstants.TEST_EXECUTION_FEEDBACK, TestCaseMapper.MAPPER.convertToDto(s)));
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
