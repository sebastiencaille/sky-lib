package ch.scaille.tcwriter.it;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.config.FsConfigManager;
import ch.scaille.tcwriter.config.TCConfig;
import ch.scaille.tcwriter.testexec.JUnitTestExecutor;
import ch.scaille.tcwriter.testexec.JunitTestExecConfig;
import ch.scaille.tcwriter.generators.JavaToDictionary;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.it.api.TestSessionRole;
import ch.scaille.tcwriter.it.api.TestWriterRole;
import ch.scaille.tcwriter.model.persistence.FsModelConfig;
import ch.scaille.tcwriter.model.persistence.FsModelDao;
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
	public void startGui() throws  InvocationTargetException, InterruptedException {
		final var tcPath = new File(RESOURCE_FOLDER, "testCase");
		tcPath.mkdirs();
		final var dictionaries = new File(RESOURCE_FOLDER, "dictionaries");
		dictionaries.mkdirs();
		
		// Setup config
		
		final var modelConfig = new FsModelConfig();
		modelConfig.setTcPath(tcPath.toString());
		modelConfig.setTcExportPath("./src/test/java");
		modelConfig.setDictionaryPath(dictionaries.toString());
		modelConfig.setTemplatePath(new File("rsrc:templates/TC.template").toString());

		final var junitTestConfig = new JunitTestExecConfig();
		junitTestConfig.setJava("");
		junitTestConfig.setClasspath("");
		
		final var configLoader = new FsConfigManager(RESOURCE_FOLDER.toPath()).setConfiguration(TCConfig.of("default", modelConfig, junitTestConfig));
		
		// Setup services 
		final var persister = new FsModelDao(configLoader);
		final var executor = new JUnitTestExecutor(configLoader, persister, ClassLoaderHelper.appClassPath());

		// Setup data
		final var dictionary = new JavaToDictionary(TestWriterRole.class, TestSessionRole.class, AbstractGuiTest.class)
				.generate();
		persister.writeTestDictionary(dictionary);


		final var controller = new TCWriterController(configLoader, persister, dictionary, executor);
		SwingUtilities.invokeAndWait(controller::run);

		pilot = new TCGuiPilot(controller.getGui());

		final var localRole = new LocalTCWriterRole(pilot);
		tcWriter = localRole;
		testSession = localRole;
	}

	@AfterEach
	public void closeGui() {
		pilot.close();
		Logs.of(getClass()).info(pilot.getActionReport().getFormattedReport());
	}

}
