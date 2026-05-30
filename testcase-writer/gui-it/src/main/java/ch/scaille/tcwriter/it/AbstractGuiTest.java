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
import org.jspecify.annotations.Nullable;
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
import ch.scaille.tcwriter.javatc.generators.JavaToDictionary;
import ch.scaille.tcwriter.services.generators.visitors.HumanReadableVisitor;
import ch.scaille.tcwriter.javatc.recorder.TestCaseRecorder;
import ch.scaille.tcwriter.javatc.testexec.JUnitTestExecutor;
import ch.scaille.tcwriter.javatc.testexec.recorder.ITestCaseRecorder;
import ch.scaille.tcwriter.javatc.testexec.recorder.TestCaseRecorderAspect;
import ch.scaille.util.helpers.JavaExt;
import ch.scaille.util.helpers.LambdaExt;
import lombok.extern.java.Log;

@TCActors({
        @TCActor(variable = "tcWriter", humanReadable = "test writer", description = "Test writer", role = TestWriterRole.class),
        @TCActor(variable = "testSession", humanReadable = "test session", description = "A test session", role = TestSessionRole.class)
})
@Log
public class AbstractGuiTest {

    private static final Path RESOURCE_FOLDER = Paths.get(System.getProperty("java.io.tmpdir"));

    @Nullable
    private TCGuiPilot pilot;

    @Nullable
    protected TestWriterRole tcWriter;

    @Nullable
    protected TestSessionRole testSession;

    @Nullable
    private ITestCaseRecorder testRecorder;

    @BeforeEach
    public void startGui() throws InvocationTargetException, InterruptedException, IOException {
        final var tcPath = RESOURCE_FOLDER.resolve("testcase");
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
        junitTestConfig.setClasspath();

        final var daoConfig = DaoConfigs.withFolder(DaoConfigs.tempFolder(), false, getClass().getModule());
        final var configDao = daoConfig.configDao()
                .setConfiguration(TCConfig.of("default", modelConfig, junitTestConfig));

        // Setup services
        final var persister = daoConfig.modelDao();
        final var executor = new JUnitTestExecutor(configDao, persister, JavaExt.locationOf(TCWriterController.class).resolve("../javatc-resources"));

        // Setup data
        final var dictionary = new JavaToDictionary("gui-it", TestWriterRole.class, TestSessionRole.class, AbstractGuiTest.class)
                .generate();
        dictionary.getMetadata().setDescription("Basic tcWriter tests");
        persister.writeTestDictionary(dictionary);

        final var controller = new TCWriterController(configDao, persister, dictionary, executor);
        SwingUtilities.invokeAndWait(controller::start);

        pilot = new TCGuiPilot(controller.getGui())
           .configure(config -> config.defaultPollingTimeout(Duration.ofSeconds(1)));

        tcWriter = RecorderTestActors.register(new LocalTCWriterRole(pilot), "tcWriter", null);
        testSession = RecorderTestActors.register(new LocalTCWriterRole(pilot), "testSession", null);

        testRecorder = new TestCaseRecorder(dictionary);
        TestCaseRecorderAspect.setRecorder(testRecorder);
    }

    @AfterEach
    public void closeGui(TestInfo testInfo) {
        if (testRecorder != null) {
            final var recordedTest = testRecorder.buildTestCase(testInfo.getTestMethod().map(Method::getName).orElseThrow());
            log.info(() -> new HumanReadableVisitor(recordedTest, false).processAllSteps());
        }

        if (pilot != null) {
            pilot.close();
            log.info(() -> pilot.getActionReport().getFormattedReport());
        }
    }

}
