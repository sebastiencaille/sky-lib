package ch.scaille.tcwriter.it;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.executors.ITestExecutor;
import ch.scaille.tcwriter.executors.JunitTestExecutor;
import ch.scaille.tcwriter.generators.JavaToDictionary;
import ch.scaille.tcwriter.generators.TCConfig;
import ch.scaille.tcwriter.generators.model.persistence.JsonModelPersister;
import ch.scaille.tcwriter.generators.model.testapi.TestDictionary;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.tcwriter.it.api.TestSessionRole;
import ch.scaille.tcwriter.it.api.TestWriterRole;
import ch.scaille.util.helpers.ClassLoaderHelper;

@TCActors({ "tcWriter|TestWriterRole|Test writer|test writer",
		"testSession|TestSessionRole|Test session|test session" })
public class AbstractGuiTest {

	private static final File RESOURCE_FOLDER = new File("./src/main/resources");
	protected TestWriterRole tcWriter;
	protected TestSessionRole testSession;
	private TCGuiPilot pilot;

	@BeforeEach
	public void startGui() throws IOException, InvocationTargetException, InterruptedException {
		final TCConfig config = new TCConfig();
		final File tcPath = new File(RESOURCE_FOLDER, "testCase");
		tcPath.mkdirs();
		final File modelPath = new File(RESOURCE_FOLDER, "models");
		modelPath.mkdirs();

		config.setTcPath(tcPath.toString());
		config.setTCExportPath("./src/test/java");
		config.setDictionaryPath(modelPath + "/test-model.json");
		config.setTemplatePath(new File("rsrc:templates/TC.template").toString());
		final JsonModelPersister persister = new JsonModelPersister(config);

		final TestDictionary model = new JavaToDictionary(TestWriterRole.class, TestSessionRole.class,
				AbstractGuiTest.class).generate();
		persister.writeTestDictionary(model);

		final ITestExecutor executor = new JunitTestExecutor(persister, ClassLoaderHelper.appClassPath());

		final TCWriterController controller = new TCWriterController(persister, executor);
		SwingUtilities.invokeAndWait(controller::run);

		pilot = new TCGuiPilot(controller.getGui());

		final LocalTCWriterRole localRole = new LocalTCWriterRole(pilot);
		tcWriter = localRole;
		testSession = localRole;
	}

	@AfterEach
	public void closeGui() {
		pilot.close();
		System.out.print(pilot.getActionReport().getFormattedReport());
	}

}