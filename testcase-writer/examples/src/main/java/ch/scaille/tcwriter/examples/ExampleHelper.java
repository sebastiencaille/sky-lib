package ch.scaille.tcwriter.examples;

import static ch.scaille.tcwriter.persistence.factory.DaoConfigs.cp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.scaille.generators.util.CodeGeneratorParams;
import ch.scaille.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.scaille.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.persistence.IConfigDao;
import ch.scaille.tcwriter.persistence.ModelConfig;
import ch.scaille.tcwriter.persistence.ModelDao;
import ch.scaille.tcwriter.persistence.factory.DaoConfigs;
import ch.scaille.tcwriter.persistence.testexec.JunitTestExecConfig;
import ch.scaille.tcwriter.services.generators.JavaToDictionary;
import ch.scaille.tcwriter.services.generators.visitors.HumanReadableVisitor;
import ch.scaille.tcwriter.services.recorder.TestCaseRecorder;
import ch.scaille.tcwriter.services.recorder.TestCaseRecorderAspect;
import ch.scaille.tcwriter.services.testexec.ITestExecutor;
import ch.scaille.tcwriter.services.testexec.JUnitTestExecutor;
import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.Logs;

/**
 * To set basic dictionary / test / configuration up
 */
public class ExampleHelper {

	private static final Path RESOURCE_FOLDER = Paths.get(System.getProperty("java.io.tmpdir"));

	public static final String TC_NAME = "testCase";
	
	public static final String TC_FILE = "ch/scaille/tcwriter/examples/GeneratedTest.java";

	private final IConfigDao configDao;

	private final ModelDao modelDao;

	public ExampleHelper() throws IOException {
		this(RESOURCE_FOLDER, "default");
	}

	/**
	 * Configure the example environment
	 */
	public ExampleHelper(Path dataPath, String configName) throws IOException {
		final var tcPath = dataPath.resolve("testcase");
		Files.createDirectories(tcPath);

		final var dictionaryPath = dataPath.resolve("dictionary");
		Files.createDirectories(dictionaryPath);

		final var modelConfig = new ModelConfig();
		modelConfig.setTcPath(tcPath.toString());
		modelConfig.setTcExportPath(
				CodeGeneratorParams.mavenTargetFolderOf(ExampleHelper.class).resolve("generated-tests").toString());
		modelConfig.setDictionaryPath(dictionaryPath.toString());
		modelConfig.setTemplatePath(cp("templates/TC.template"));

		final var junitTestConfig = new JunitTestExecConfig();
		junitTestConfig.setClasspath("");

		final var daoConfig = DaoConfigs.withFolder(dataPath);
		configDao = daoConfig.configDao().setConfiguration(TCConfig.of(configName, modelConfig, junitTestConfig));
		modelDao = daoConfig.modelDao();
	}

	public ModelDao getModelDao() {
		return modelDao;
	}

	public TestDictionary generateDictionary() {
		final var dictionary = new JavaToDictionary("BasicTest", CustomerTestRole.class, DeliveryTestRole.class).generate();
		dictionary.getMetadata().setDescription("Test dictionary");
		return dictionary;
	}

	public ExportableTestCase recordTestCase(final TestDictionary dictionary) {

		final var recorder = new TestCaseRecorder(dictionary);
		TestCaseRecorderAspect.setRecorder(recorder);

		// create and run a test
		final var test = new SimpleTest();
		test.initActors();
		test.testNormalCase();

		final var testCase = recorder.buildTestCase("ch.scaille.tcwriter.examples.GeneratedTest");
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
