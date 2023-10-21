package ch.scaille.tcwriter.server.webapi.controllers;

import static ch.scaille.tcwriter.server.webapi.controllers.exceptions.ValidationHelper.validateDictionarySet;
import static ch.scaille.tcwriter.server.webapi.controllers.exceptions.ValidationHelper.validateTestCaseSet;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.core.JsonProcessingException;

import ch.scaille.tcwriter.generated.api.controllers.TestcaseApiController;
import ch.scaille.tcwriter.generated.api.model.ExportType;
import ch.scaille.tcwriter.generated.api.model.Metadata;
import ch.scaille.tcwriter.generated.api.model.TestCase;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.server.facade.ContextFacade;
import ch.scaille.tcwriter.server.facade.TestCaseFacade;
import ch.scaille.tcwriter.server.web.controller.exceptions.WebRTException;
import ch.scaille.tcwriter.server.webapi.config.WebsocketConfig;
import ch.scaille.tcwriter.server.webapi.mappers.MetadataMapper;
import ch.scaille.tcwriter.server.webapi.mappers.TestCaseMapper;
import io.swagger.v3.core.util.Json;
import jakarta.validation.Valid;

public class TestCaseController extends TestcaseApiController {

	private final ContextFacade contextFacade;

	private final TestCaseFacade testCaseFacade;

	private final MessageSendingOperations<String> feedbackSendingTemplate;

	public TestCaseController(ContextFacade contextFacade, TestCaseFacade testCaseFacade,
			MessageSendingOperations<String> feedbackSendingTemplate, NativeWebRequest request) {
		super(request);
		this.contextFacade = contextFacade;
		this.testCaseFacade = testCaseFacade;
		this.feedbackSendingTemplate = feedbackSendingTemplate;
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
	public ResponseEntity<Void> executeTestCase(@Valid String tc) {
		final var loadedTC = loadValidTestCase(tc);
		testCaseFacade.executeTest(loadedTC,
				s -> feedbackSendingTemplate.convertAndSend(WebsocketConfig.TEST_FEEDBACK_DESTINATION,
						new GenericMessage<>(TestCaseMapper.MAPPER.convert(s))));
		return ResponseEntity.ok(null);
	}

	@Override
	public ResponseEntity<String> exportTestCase(@Valid String tc, @Valid ExportType format) {
		final var loadedTC = loadValidTestCase(tc);
		if (format == ExportType.JAVA) {
			return  exportJava(loadedTC);
		}
		return exportHumanReadable(loadedTC);
	}

	private ResponseEntity<String> exportHumanReadable(ExportableTestCase tc) {
		try {
			var headers = new org.springframework.http.HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			return new ResponseEntity<>(
					Json.mapper().writeValueAsString(testCaseFacade.computeHumanReadableTexts(tc, tc.getSteps())),
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
			tcId = validateTestCaseSet(contextFacade.get().getTestCase());
		} else {
			tcId = tc;
		}
		return testCaseFacade.load(tcId, dictionaryId);
	}

	private String getCurrentDictionaryId() {
		return validateDictionarySet(contextFacade.get().getDictionary());
	}
}
