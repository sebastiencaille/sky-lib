package ch.scaille.tcwriter.it;

import static ch.scaille.tcwriter.persistence.factory.DaoConfigs.cp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import javax.swing.SwingUtilities;

import ch.scaille.tcwriter.annotations.TCActor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.it.api.TestSessionRole;
import ch.scaille.tcwriter.it.api.TestWriterRole;
import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.persistence.ModelConfig;
import ch.scaille.tcwriter.persistence.factory.DaoConfigs;
import ch.scaille.tcwriter.persistence.testexec.JunitTestExecConfig;
import ch.scaille.tcwriter.recorder.RecorderTestActors;
import ch.scaille.tcwriter.services.generators.JavaToDictionary;
import ch.scaille.tcwriter.services.generators.visitors.HumanReadableVisitor;
import ch.scaille.tcwriter.services.recorder.ITestCaseRecorder;
import ch.scaille.tcwriter.services.recorder.TestCaseRecorder;
import ch.scaille.tcwriter.services.recorder.TestCaseRecorderAspect;
import ch.scaille.tcwriter.services.testexec.JUnitTestExecutor;
import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.LambdaExt;
import ch.scaille.util.helpers.Logs;

@TCActors({
        @TCActor(variable = "tcWriter", humanReadable = "test writer", description = "Test writer", role = TestWriterRole.class),
        @TCActor(variable = "testSession", humanReadable = "test session", description = "A test session", role = TestSessionRole.class)
})
public class AbstractGuiTest {

    private static final Path RESOURCE_FOLDER = Paths.get(System.getProperty("java.io.tmpdir"));

    private TCGuiPilot pilot;

    protected TestWriterRole tcWriter;
    protected TestSessionRole testSession;

    private ITestCaseRecorder testRecorder;

    @BeforeEach
    public void startGui() throws InvocationTargetException, InterruptedException, IOException {
        final var tcPath = RESOURCE_FOLDER.resolve("testCase");
        if (!Files.exists(tcPath)) {
            Files.createDirectory(tcPath);
        }
        try (var stream = Files.list(tcPath)) {
            stream.forEach(LambdaExt.uncheckedC(Files::delete));
        }

        final var dictionariesPath = RESOURCE_FOLDER.resolve("dictionaries");
        if (!Files.exists(dictionariesPath)) {
            Files.createDirectory(dictionariesPath);
        }


        // Setup config
        final var modelConfig = new ModelConfig();
        modelConfig.setTcPath(tcPath.toString());
        modelConfig.setTcExportPath("./src/test/java");
        modelConfig.setDictionaryPath(dictionariesPath.toString());
        modelConfig.setTemplatePath(cp("templates"));

        final var junitTestConfig = new JunitTestExecConfig();
        junitTestConfig.setJava("");
        junitTestConfig.setClasspath("");

        final var daoConfig = DaoConfigs.withFolder(DaoConfigs.tempFolder());
        final var configDao = daoConfig.configDao()
                .setConfiguration(TCConfig.of("default", modelConfig, junitTestConfig));

        // Setup services
        final var persister = daoConfig.modelDao();
        final var executor = new JUnitTestExecutor(configDao, persister, ClassLoaderHelper.appClassPath());

        // Setup data
        final var dictionary = new JavaToDictionary("gui-it", TestWriterRole.class, TestSessionRole.class, AbstractGuiTest.class)
                .generate();
        dictionary.getMetadata().setDescription("Basic tcwriter tests");
        persister.writeTestDictionary(dictionary);

        final var controller = new TCWriterController(configDao, persister, dictionary, executor);
        SwingUtilities.invokeAndWait(controller::start);

        pilot = new TCGuiPilot(controller.getGui());
        pilot.setDefaultPollingTimeout(Duration.ofSeconds(5));

        tcWriter = RecorderTestActors.register(new LocalTCWriterRole(pilot), "tcWriter", null);
        testSession = RecorderTestActors.register(new LocalTCWriterRole(pilot), "testSession", null);

        testRecorder = new TestCaseRecorder(dictionary);
        TestCaseRecorderAspect.setRecorder(testRecorder);
    }

    @AfterEach
    public void closeGui(TestInfo testInfo) {
        if (pilot == null) {
            return;
        }
        final var recordedTest = testRecorder.buildTestCase(testInfo.getTestMethod().map(Method::getName).orElseThrow());
        Logs.of(getClass()).info(() -> new HumanReadableVisitor(recordedTest, false).processAllSteps());

        pilot.close();
        Logs.of(getClass()).info(pilot.getActionReport().getFormattedReport());
    }

}
