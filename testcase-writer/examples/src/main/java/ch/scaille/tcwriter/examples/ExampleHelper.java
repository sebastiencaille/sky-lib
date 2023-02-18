package ch.scaille.tcwriter.examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.scaille.generators.util.CodeGeneratorParams;
import ch.scaille.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.scaille.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.scaille.tcwriter.executors.ITestExecutor;
import ch.scaille.tcwriter.executors.JunitTestExecutor;
import ch.scaille.tcwriter.generators.JavaToDictionary;
import ch.scaille.tcwriter.generators.visitors.HumanReadableVisitor;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.persistence.FsModelDao;
import ch.scaille.tcwriter.model.persistence.FsTCConfig;
import ch.scaille.tcwriter.model.persistence.IModelDao;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.recorder.TestCaseRecorder;
import ch.scaille.tcwriter.recorder.TestCaseRecorderAspect;
import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.Logs;

public class ExampleHelper {

    private static final Path RESOURCE_FOLDER = Paths.get(System.getProperty("java.io.tmpdir"));

    public static final String TC_NAME = "testCase";

    private final IModelDao modelDao;

    public ExampleHelper() throws IOException {
        this(RESOURCE_FOLDER);
    }

    public ExampleHelper(Path dataPath) throws IOException {
        final Path tcPath = dataPath.resolve( "testcase");
		Files.createDirectories(tcPath);
        final Path modelPath = dataPath.resolve("dictionary");
		Files.createDirectories(modelPath);

        final var config = new FsTCConfig();
        config.setTcPath(tcPath.toString());
        config.setTCExportPath(CodeGeneratorParams.mavenTarget(ExampleHelper.class).resolve("generated-tests").toString());
        config.setDictionaryPath(modelPath + "/test-dictionary.json");
        config.setTemplatePath("rsrc:templates/TC.template");
        modelDao = new FsModelDao(config);
    }

    public IModelDao getModelDao() {
        return modelDao;
    }

    public TestDictionary generateDictionary() {
        TestDictionary dictionary = new JavaToDictionary(CustomerTestRole.class, DeliveryTestRole.class).generate();
        dictionary.getMetadata().setDescription("Test dictionary");
        return dictionary;
    }

    public TestCase recordTestCase(final TestDictionary model) {
        // Setup the recorder
        final var recorder = new TestCaseRecorder(modelDao, model);
        TestCaseRecorderAspect.setRecorder(recorder);

        // create and run a test
        final var test = new SimpleTest();
        test.initActors();
        test.testNormalCase();

        // retrieve the test
        final var testCase = recorder.getTestCase("ch.scaille.tcwriter.examples.GeneratedTest");
        Logs.of(ExampleHelper.class).info(() -> new HumanReadableVisitor(testCase, true).processAllSteps());
        return testCase;
    }

    public ITestExecutor testExecutor() {
        return new JunitTestExecutor(getModelDao(), ClassLoaderHelper.appClassPath());
    }
}
