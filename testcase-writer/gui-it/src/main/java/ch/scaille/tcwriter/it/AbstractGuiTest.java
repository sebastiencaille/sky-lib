package ch.scaille.tcwriter.it;

import static ch.scaille.tcwriter.persistence.factory.DaoConfigs.cp;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.it.api.TestSessionRole;
import ch.scaille.tcwriter.it.api.TestWriterRole;
import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.persistence.ModelConfig;
import ch.scaille.tcwriter.persistence.factory.DaoConfigs;
import ch.scaille.tcwriter.persistence.testexec.JunitTestExecConfig;
import ch.scaille.tcwriter.services.generators.JavaToDictionary;
import ch.scaille.tcwriter.services.testexec.JUnitTestExecutor;
import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.Logs;

@TCActors({ "tcWriter|TestWriterRole|Test writer|test writer",
		"testSession|TestSessionRole|Test session|test session" })
public class AbstractGuiTest {

	private static final File RESOURCE_FOLDER = new File(System.getProperty("java.io.tmpdir"));

	private TCGuiPilot pilot;

	protected TestWriterRole tcWriter;
	protected TestSessionRole testSession;

	@BeforeEach
	public void startGui() throws InvocationTargetException, InterruptedException {
		final var tcPath = new File(RESOURCE_FOLDER, "testCase");
		tcPath.mkdirs();
		Arrays.stream(tcPath.listFiles()).forEach(File::delete);

		final var dictionariesPath = new File(RESOURCE_FOLDER, "dictionaries");
		dictionariesPath.mkdirs();

		// Setup config

		final var modelConfig = new ModelConfig();
		modelConfig.setTcPath(tcPath.toString());
		modelConfig.setTcExportPath("./src/test/java");
		modelConfig.setDictionaryPath(dictionariesPath.toString());
		modelConfig.setTemplatePath(cp("templates/TC.template"));

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
		final var dictionary = new JavaToDictionary(TestWriterRole.class, TestSessionRole.class, AbstractGuiTest.class)
				.generate();
		persister.writeTestDictionary(dictionary);

		final var controller = new TCWriterController(configDao, persister, dictionary, executor);
		SwingUtilities.invokeAndWait(controller::run);

		pilot = new TCGuiPilot(controller.getGui());
		pilot.setDefaultPollingTimeout(Duration.ofSeconds(5));

		final var localRole = new LocalTCWriterRole(pilot);
		tcWriter = localRole;
		testSession = localRole;
	}

	@AfterEach
	public void closeGui() {
		if (pilot == null) {
			return;
		}
		pilot.close();
		Logs.of(getClass()).info(pilot.getActionReport().getFormattedReport());
	}

}
