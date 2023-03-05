package ch.scaille.tcwriter.server.webapi.controllers;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.executors.ITestExecutor;
import ch.scaille.tcwriter.executors.JUnitTestExecutor;
import ch.scaille.tcwriter.generated.api.controllers.TestcaseApiController;
import ch.scaille.tcwriter.generated.api.model.Metadata;
import ch.scaille.tcwriter.generated.api.model.TestCase;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.persistence.IModelDao;
import ch.scaille.tcwriter.server.dao.IDictionaryDao;
import ch.scaille.tcwriter.server.dao.ITestCaseDao;
import ch.scaille.tcwriter.server.services.ContextService;
import ch.scaille.tcwriter.server.services.TestCaseService;
import ch.scaille.tcwriter.server.webapi.mappers.MetadataMapper;
import ch.scaille.tcwriter.server.webapi.mappers.TestCaseMapper;
import ch.scaille.tcwriter.testexec.TestExecutionListener;
import ch.scaille.tcwriter.testexec.TestRemoteControl;
import ch.scaille.util.helpers.ClassLoaderHelper;

public class TestCaseController extends TestcaseApiController {

	private static final Logger LOGGER = Logger.getLogger(TestCaseController.class.getName());

	private final IDictionaryDao dictionaryDao;

	private final ITestCaseDao testCaseDao;

	private final ContextService contextService;

	private final TestCaseService tcService;

	private final IModelDao modelDao;

	public TestCaseController(ContextService contextService, IDictionaryDao dictionaryDao, ITestCaseDao testcaseDao,
			IModelDao modelDao, TestCaseService tcService, NativeWebRequest request) {
		super(request);
		this.contextService = contextService;
		this.dictionaryDao = dictionaryDao;
		this.testCaseDao = testcaseDao;
		this.modelDao = modelDao;
		this.tcService = tcService;
	}

	@Override
	public ResponseEntity<List<Metadata>> listAll() {
		var currentDictionary = contextService.get().getDictionary();
		if (currentDictionary == null) {
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
		}
		return new ResponseEntity<>(testCaseDao.listAll(dictionaryDao.load(currentDictionary)).stream()
				.map(MetadataMapper.MAPPER::convert).toList(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<TestCase> testcase(String tc) {
		var currentDictionaryId = contextService.get().getDictionary();
		var currentTCId = contextService.get().getTestCase();
		if (currentDictionaryId == null || currentTCId == null) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		var loadedTC = loadTestCase(tc, dictionaryDao.load(contextService.get().getDictionary()));
		var dto = TestCaseMapper.MAPPER.convert(loadedTC);
		var humanReadables = tcService.computeHumanReadableTexts(loadedTC, loadedTC.getSteps());
		for (int i = 0; i < dto.getSteps().size(); i++) {
			dto.getSteps().get(i).setHumanReadable(humanReadables.get(i));
		}
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Void> executeTestcase(String tc) {
		var testRemoteControl = new TestRemoteControl(10_000, new TestExecutionListener() {
			@Override
			public void testRunning(boolean running) {
				LOGGER.info(() -> "Running: " + running);
			}

			@Override
			public void testPaused(boolean paused) {
				LOGGER.info(() -> "Paused: " + paused);
			}
		});
		var tcpPort = testRemoteControl.prepare();
		var executor = new JUnitTestExecutor(modelDao, ClassLoaderHelper.guessClassPath());
		try (var config = new ITestExecutor.TestConfig(
				loadTestCase(tc, dictionaryDao.load(contextService.get().getDictionary())),
				Paths.get(System.getProperty("java.io.tmpdir")), tcpPort)) {
			executor.startTest(config);
			testRemoteControl.controlTest();
		} catch (IOException | InterruptedException | TestCaseException e) {
			throw new RuntimeException("Web call execution failed", e);
		}
		return new ResponseEntity<>(null, HttpStatus.OK);
	}

	private ch.scaille.tcwriter.model.testcase.ExportableTestCase loadTestCase(String tc,
			TestDictionary testDictionary) {
		if ("current".equals(tc)) {
			return testCaseDao.load(contextService.get().getTestCase(), testDictionary);
		}
		return testCaseDao.load(tc, testDictionary);
	}
}
