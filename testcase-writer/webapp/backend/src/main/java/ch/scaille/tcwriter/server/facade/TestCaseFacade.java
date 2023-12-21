package ch.scaille.tcwriter.server.facade;

import static ch.scaille.tcwriter.server.webapi.controllers.exceptions.ValidationHelper.testCaseFound;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import ch.scaille.tcwriter.model.Metadata;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestStep;
import ch.scaille.tcwriter.model.testexec.StepStatus;
import ch.scaille.tcwriter.server.dao.IDictionaryDao;
import ch.scaille.tcwriter.server.dao.ITestCaseDao;
import ch.scaille.tcwriter.server.web.controller.exceptions.WebRTException;
import ch.scaille.tcwriter.services.generators.visitors.HumanReadableVisitor;
import ch.scaille.tcwriter.services.testexec.ITestExecutor;
import ch.scaille.tcwriter.services.testexec.JUnitTestExecutor;
import ch.scaille.tcwriter.services.testexec.TestExecutionListener;
import ch.scaille.tcwriter.services.testexec.TestRemoteControl;

public class TestCaseFacade extends AbstractFacade {

	private static final Logger LOGGER = Logger.getLogger(TestCaseFacade.class.getName());

	private final ITestCaseDao testCaseDao;

	private final JUnitTestExecutor testExecutor;

	public TestCaseFacade(IDictionaryDao dictionaryDao, ITestCaseDao testCaseDao, JUnitTestExecutor testExecutor) {
		super(dictionaryDao);
		this.testCaseDao = testCaseDao;
		this.testExecutor = testExecutor;
	}

	public Collection<Metadata> listAll(String dictionaryId) {
		return testCaseDao.listAll(loadDictionary(dictionaryId));
	}

	public ExportableTestCase load(String tcId, String dictionaryId) {
		return testCaseFound(tcId, testCaseDao.load(tcId, loadDictionary(dictionaryId)));
	}

	public List<String> computeHumanReadableTexts(TestCase tc, List<TestStep> steps) {
		final var humanReadableVisitor = new HumanReadableVisitor(tc, false);
		return steps.stream().map(humanReadableVisitor::process).toList();
	}

	public void executeTest(final ExportableTestCase loadedTC, Consumer<StepStatus> feedback) {
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
			for (int i = f; i <= t; i++) {
				final var status = testRemoteControl.stepStatus(i);
				LOGGER.info(status.toString());
				feedback.accept(status);
			}
		});

		final var tcpPort = testRemoteControl.prepare();
		Path tempDir;
		try {
			tempDir = Files.createTempDirectory("tc-writer");
		} catch (IOException e) {
			throw new IllegalStateException("Web call execution failed", e);
		}
		try (var config = new ITestExecutor.TestConfig(loadedTC, tempDir, tcpPort)) {
			testExecutor.startTest(config);
			testRemoteControl.controlTest(loadedTC.getSteps().size());
		} catch (IOException | InterruptedException | TestCaseException e) {
			Thread.interrupted();
			throw new WebRTException(e);
		}
	}

	public String generateCode(ExportableTestCase tc) {
		try {
			return testExecutor.createTemplate(tc).generate();
		} catch (TestCaseException e) {
			throw new WebRTException(e);
		}
	}


}
