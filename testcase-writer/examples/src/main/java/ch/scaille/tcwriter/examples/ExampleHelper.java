package ch.scaille.tcwriter.examples;

import java.io.File;
import java.io.IOException;

import ch.scaille.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.scaille.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.scaille.tcwriter.executors.ITestExecutor;
import ch.scaille.tcwriter.executors.JunitTestExecutor;
import ch.scaille.tcwriter.generators.JavaToDictionary;
import ch.scaille.tcwriter.generators.TCConfig;
import ch.scaille.tcwriter.generators.visitors.HumanReadableVisitor;
import ch.scaille.tcwriter.model.persistence.FsModelDao;
import ch.scaille.tcwriter.model.persistence.IModelDao;
import ch.scaille.tcwriter.model.testapi.TestDictionary;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.recorder.TestCaseRecorder;
import ch.scaille.tcwriter.recorder.TestCaseRecorderAspect;
import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.Logs;

public class ExampleHelper {

	private ExampleHelper() {
	}

	private static final File RESOURCE_FOLDER = new File("./src/main/resources");

	private static final IModelDao modelDao;

	public static final String TC_NAME = "testCase.json";

	static {
		final File tcPath = new File(RESOURCE_FOLDER, "testCase");
		tcPath.mkdirs();
		final File modelPath = new File(RESOURCE_FOLDER, "models");
		modelPath.mkdirs();

		var config = new TCConfig();
		config.setTcPath(tcPath.toString());
		config.setTCExportPath("target/generated-tests");
		config.setDictionaryPath(modelPath + "/test-model.json");
		config.setTemplatePath("rsrc:templates/TC.template");
		modelDao = new FsModelDao(config);
	}

	public static IModelDao getModelDao() {
		return modelDao;
	}

	public static TestDictionary generateDictionary() {
		return new JavaToDictionary(CustomerTestRole.class, DeliveryTestRole.class).generate();
	}

	public static void saveDictionary(final TestDictionary dictionary) throws IOException {
		modelDao.writeTestDictionary(dictionary);
	}

	public static void saveTC(final String name, final TestCase testCase) throws IOException {
		modelDao.writeTestCase(name, testCase);
	}

	public static TestCase recordTestCase(final TestDictionary model) {
		final var recorder = new TestCaseRecorder(modelDao, model);
		TestCaseRecorderAspect.setRecorder(recorder);
		final var test = new SimpleTest();
		test.initActors();
		test.testNormalCase();
		final var testCase = recorder.getTestCase("ch.scaille.tcwriter.examples.GeneratedTest");
		Logs.of(ExampleHelper.class).info(() -> new HumanReadableVisitor(testCase, true).processAllSteps());
		return testCase;
	}

	public static ITestExecutor testExecutor() {
		return new JunitTestExecutor(getModelDao(), ClassLoaderHelper.appClassPath());
	}
}
