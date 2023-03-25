package ch.scaille.tcwriter.server.webapi.controllers;

import static ch.scaille.tcwriter.server.webapi.controllers.ControllerHelper.validateDictionarySet;
import static ch.scaille.tcwriter.server.webapi.controllers.ControllerHelper.validateTestCaseSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.executors.ITestExecutor;
import ch.scaille.tcwriter.executors.JUnitTestExecutor;
import ch.scaille.tcwriter.generated.api.controllers.TestcaseApiController;
import ch.scaille.tcwriter.generated.api.model.Metadata;
import ch.scaille.tcwriter.generated.api.model.TestCase;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.persistence.IModelDao;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.server.dao.IDictionaryDao;
import ch.scaille.tcwriter.server.dao.ITestCaseDao;
import ch.scaille.tcwriter.server.services.ContextService;
import ch.scaille.tcwriter.server.services.TestCaseService;
import ch.scaille.tcwriter.server.webapi.config.WebsocketConfig;
import ch.scaille.tcwriter.server.webapi.controllers.exceptions.TestCaseNotFoundException;
import ch.scaille.tcwriter.server.webapi.mappers.MetadataMapper;
import ch.scaille.tcwriter.server.webapi.mappers.TestCaseMapper;
import ch.scaille.tcwriter.testexec.TestExecutionListener;
import ch.scaille.tcwriter.testexec.TestRemoteControl;
import ch.scaille.util.helpers.ClassLoaderHelper;
import jakarta.validation.Valid;

public class TestCaseController extends TestcaseApiController {

	private static final Logger LOGGER = Logger.getLogger(TestCaseController.class.getName());

	private final IDictionaryDao dictionaryDao;

	private final ITestCaseDao testCaseDao;

	private final ContextService contextService;

	private final TestCaseService tcService;

	private final IModelDao modelDao;

	private final MessageSendingOperations<String> feedbackSendingTemplate;

	public TestCaseController(ContextService contextService, IDictionaryDao dictionaryDao, ITestCaseDao testcaseDao,
			IModelDao modelDao, TestCaseService tcService, MessageSendingOperations<String> feedbackSendingTemplate,
			NativeWebRequest request) {
		super(request);
		this.contextService = contextService;
		this.dictionaryDao = dictionaryDao;
		this.testCaseDao = testcaseDao;
		this.modelDao = modelDao;
		this.tcService = tcService;
		this.feedbackSendingTemplate = feedbackSendingTemplate;
	}

	@Override
	public ResponseEntity<List<Metadata>> listAll() {
		var dictionary = loadValidDictionary();
		return ResponseEntity.ok(testCaseDao.listAll(dictionary).stream().map(MetadataMapper.MAPPER::convert).toList());
	}

	@Override
	public ResponseEntity<TestCase> testcase(@Valid String tc) {
		final var loadedTC = loadValidTestCase(tc);
		final var dto = TestCaseMapper.MAPPER.convert(loadedTC);
		final var humanReadables = tcService.computeHumanReadableTexts(loadedTC, loadedTC.getSteps());
		for (int i = 0; i < dto.getSteps().size(); i++) {
			dto.getSteps().get(i).setHumanReadable(humanReadables.get(i));
		}
		return ResponseEntity.ok(dto);
	}

	@Override
	public ResponseEntity<Void> executeTestcase(@Valid String tc) {
		final var loadedTC = loadValidTestCase(tc);
		final var testRemoteControl = new TestRemoteControl(10_000, new TestExecutionListener() {
			@Override
			public void testRunning(boolean running) {
				LOGGER.info(() -> "Running: " + running);
			}

			@Override
			public void testPaused(boolean paused) {
				LOGGER.info(() -> "Paused: " + paused);
			}
		});

		testRemoteControl.setStepListener((f, t) -> {
			for (int i = f; i <= t;i++) {
				var status = testRemoteControl.stepStatus(i);
				LOGGER.info(status.toString());
				var msg = new GenericMessage<>(TestCaseMapper.MAPPER.convert(status));
				feedbackSendingTemplate.convertAndSend(WebsocketConfig.TEST_FEEDBACK_DESTINATION, msg);
			}
		});
		
		final var tcpPort = testRemoteControl.prepare();
		final var executor = new JUnitTestExecutor(modelDao, ClassLoaderHelper.guessClassPath());
		Path tempDir;
		try {
			tempDir = Files.createTempDirectory("tc-writer");
		} catch (IOException e) {
			throw new RuntimeException("Web call execution failed", e);
		}
		try (var config = new ITestExecutor.TestConfig(loadedTC, tempDir, tcpPort)) {
			executor.startTest(config);
			testRemoteControl.controlTest();
		} catch (IOException | InterruptedException | TestCaseException e) {
			throw new RuntimeException("Web call execution failed", e);
		}
		return ResponseEntity.ok(null);
	}

	private ExportableTestCase loadValidTestCase(String tc) {
		final var dictionary = loadValidDictionary();
		final ExportableTestCase loadedTC;
		if ("current".equals(tc)) {
			final var currentTCId = validateTestCaseSet(contextService.get().getTestCase());
			loadedTC = validateTestCaseSet(testCaseDao.load(currentTCId, dictionary));
		} else {
			loadedTC = testCaseDao.load(tc, dictionary).orElseThrow(() -> new TestCaseNotFoundException(tc));
		}
		return loadedTC;
	}

	private ch.scaille.tcwriter.model.dictionary.TestDictionary loadValidDictionary() {
		final var currentDictionaryId = validateDictionarySet(contextService.get().getDictionary());
		return validateDictionarySet(dictionaryDao.load(currentDictionaryId));
	}

}
