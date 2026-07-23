package ch.scaille.tcwriter.examples;

import static ch.scaille.tcwriter.persistence.factory.DaoConfigs.cp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.scaille.generators.util.CodeGeneratorParams;
import ch.scaille.tcwriter.examples.simple.AbstractSimpleTest;
import ch.scaille.tcwriter.examples.simple.CustomerTestRole;
import ch.scaille.tcwriter.examples.simple.DeliveryTestRole;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.javatc.generators.JavaToDictionary;
import ch.scaille.tcwriter.javatc.testexec.JUnitTestExecutor;
import ch.scaille.tcwriter.javatc.recorder.TestCaseRecorder;
import ch.scaille.tcwriter.javatc.recorder.TestCaseRecorderAspect;
import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.persistence.IConfigDao;
import ch.scaille.tcwriter.persistence.ModelConfig;
import ch.scaille.tcwriter.persistence.ModelDao;
import ch.scaille.tcwriter.persistence.factory.DaoConfigs;
import ch.scaille.tcwriter.persistence.testexec.JunitTestExecConfig;
import ch.scaille.tcwriter.services.generators.visitors.HumanReadableVisitor;
import ch.scaille.tcwriter.services.testexec.ITestExecutor;
import ch.scaille.util.helpers.JavaExt;
import lombok.Getter;
import lombok.extern.java.Log;

/**
 * To a basic dictionary / test / configuration up
 */
@Getter
@Log
public class ExampleHelper {

	private static final Path RESOURCE_FOLDER = Paths.get(System.getProperty("java.io.tmpdir"));

	public static final String TC_NAME = "testCase";
	
	public static final String TC_FILE_JAVA = "ch/scaille/tcwriter/examples/GeneratedTest.java";
	public static final String TC_FILE_YAML = "IntegrationTest.yaml";

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
		modelConfig.setTemplatePath(cp("templates"));

		final var junitTestConfig = new JunitTestExecConfig();
		junitTestConfig.setClasspath(JavaExt.locationOf(getClass()).toString());

		final var daoConfig = DaoConfigs.withFolder(dataPath, false, getClass().getModule());
		configDao = daoConfig.configDao().setConfiguration(TCConfig.of(configName, modelConfig, junitTestConfig));
		modelDao = daoConfig.modelDao();
	}

    public TestDictionary generateDictionary() {
		final var dictionary = new JavaToDictionary("SimpleTest", CustomerTestRole.class, DeliveryTestRole.class, AbstractSimpleTest.class).generate();
		dictionary.getMetadata().setDescription("Test dictionary");
		return dictionary;
	}

	public TestCase recordTestCase(final TestDictionary dictionary) {

		final var recorder = new TestCaseRecorder(dictionary);
		TestCaseRecorderAspect.setRecorder(recorder);

		// create and run a test
		final var test = new SimpleTest();
		test.initActors();
		test.testNormalCase();

		final var testCase = recorder.buildTestCase("ch.scaille.tcwriter.examples.GeneratedTest");
		log.info(() -> new HumanReadableVisitor(testCase, true).processAllSteps());

		return testCase;
	}

	public ITestExecutor testExecutor() {
			final var testJarPath = JavaExt.locationOf(TCWriterController.class).resolve("../javatc-resources");
			return new JUnitTestExecutor(configDao, modelDao, testJarPath);
	}

}
