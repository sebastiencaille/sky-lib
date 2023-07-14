package ch.scaille.tcwriter.examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.scaille.generators.util.CodeGeneratorParams;
import ch.scaille.tcwriter.config.TCConfig;
import ch.scaille.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.scaille.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.scaille.tcwriter.generators.JavaToDictionary;
import ch.scaille.tcwriter.generators.visitors.HumanReadableVisitor;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.persistence.IConfigDao;
import ch.scaille.tcwriter.model.persistence.fsconfig.FsConfigDao;
import ch.scaille.tcwriter.model.persistence.fsmodel.FsModelConfig;
import ch.scaille.tcwriter.model.persistence.fsmodel.FsModelDao;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.recorder.TestCaseRecorder;
import ch.scaille.tcwriter.recorder.TestCaseRecorderAspect;
import ch.scaille.tcwriter.testexec.ITestExecutor;
import ch.scaille.tcwriter.testexec.JUnitTestExecutor;
import ch.scaille.tcwriter.testexec.JunitTestExecConfig;
import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.Logs;

public class ExampleHelper {

	private static final Path RESOURCE_FOLDER = Paths.get(System.getProperty("java.io.tmpdir"));

	public static final String TC_NAME = "testCase";

	private final IConfigDao configDao;
	
	private final FsModelDao modelDao;

	public ExampleHelper() throws IOException {
		this(RESOURCE_FOLDER, "default");
	}

	/**
	 * Configure the example environment
	 * 
	 * @param dataPath
	 * @throws IOException
	 */
	public ExampleHelper(Path dataPath, String configName) throws IOException {
		final var tcPath = dataPath.resolve("testcase");
		Files.createDirectories(tcPath);
		final var dictionaryPath = dataPath.resolve("dictionary");
		Files.createDirectories(dictionaryPath);

		final var modelConfig = new FsModelConfig();
		modelConfig.setTcPath(tcPath.toString());
		modelConfig.setTcExportPath(
				CodeGeneratorParams.mavenTarget(ExampleHelper.class).resolve("generated-tests").toString());
		modelConfig.setDictionaryPath(dictionaryPath.toString());
		modelConfig.setTemplatePath("rsrc:templates/TC.template");

		final var junitTestConfig = new JunitTestExecConfig();
		junitTestConfig.setClasspath("");
		
		configDao = new FsConfigDao(dataPath).setConfiguration(TCConfig.of(configName, modelConfig, junitTestConfig));
		modelDao = new FsModelDao(configDao);
	}

	public FsModelDao getModelDao() {
		return modelDao;
	}

	public TestDictionary generateDictionary() {
		final var dictionary = new JavaToDictionary(CustomerTestRole.class, DeliveryTestRole.class).generate();
		dictionary.getMetadata().setDescription("Test dictionary");
		return dictionary;
	}

	public TestCase recordTestCase(final TestDictionary dictionary) {

		final var recorder = new TestCaseRecorder(modelDao, dictionary);
		TestCaseRecorderAspect.setRecorder(recorder);

		// create and run a test
		final var test = new SimpleTest();
		test.initActors();
		test.testNormalCase();

		final var testCase = recorder.getTestCase("ch.scaille.tcwriter.examples.GeneratedTest");
		Logs.of(ExampleHelper.class).info(() -> new HumanReadableVisitor(testCase, true).processAllSteps());

		return testCase;
	}

	public ITestExecutor testExecutor() {
		return new JUnitTestExecutor(configDao, getModelDao(), ClassLoaderHelper.appClassPath());
	}

	public IConfigDao getConfigDao() {
		return configDao;
	}
}
