package ch.skymarshall.tcwriter.it;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.junit.After;

import ch.skymarshall.tcwriter.executors.ITestExecutor;
import ch.skymarshall.tcwriter.executors.JunitTestExecutor;
import ch.skymarshall.tcwriter.generators.GeneratorConfig;
import ch.skymarshall.tcwriter.generators.JavaToDictionary;
import ch.skymarshall.tcwriter.generators.model.persistence.JsonModelPersister;
import ch.skymarshall.tcwriter.generators.model.testapi.TestActor;
import ch.skymarshall.tcwriter.generators.model.testapi.TestDictionary;
import ch.skymarshall.tcwriter.gui.frame.TCWriterController;
import ch.skymarshall.tcwriter.it.api.TestSessionRole;
import ch.skymarshall.tcwriter.it.api.TestWriterRole;
import ch.skymarshall.util.helpers.ClassLoaderHelper;

public class AbstractGuiTest {

	private static final File RESOURCE_FOLDER = new File("./src/main/resources");
	protected TestWriterRole tcWriter;
	protected TestSessionRole testSession;
	private TCGuiPilot pilot;

	@org.junit.Before
	public void startGui() throws IOException, InvocationTargetException, InterruptedException {

		final GeneratorConfig config = new GeneratorConfig();
		final File tcPath = new File(RESOURCE_FOLDER, "testCase");
		tcPath.mkdirs();
		final File modelPath = new File(RESOURCE_FOLDER, "models");
		modelPath.mkdirs();

		config.setTcPath(tcPath.toString());
		config.setDefaultGeneratedTCPath("./src/test/java");
		config.setDictionaryPath(modelPath + "/test-model.json");
		config.setTemplatePath(new File("templates/TC.template").toString());
		final JsonModelPersister persister = new JsonModelPersister(config);

		final TestDictionary model = new JavaToDictionary(asList(TestWriterRole.class)).generateDictionary();
		model.addActor(new TestActor("TestCase writer", "tcWriter", model.getRole(TestWriterRole.class)), null);
		persister.writeTestDictionary(model);

		final ITestExecutor executor = new JunitTestExecutor(config, ClassLoaderHelper.appClassPath());

		final TCWriterController controller = new TCWriterController(config, persister, executor);
		SwingUtilities.invokeAndWait(controller::run);
		pilot = new TCGuiPilot(controller.getGui());

		final LocalTCWriterRole localRole = new LocalTCWriterRole(pilot);
		tcWriter = localRole;
		testSession = localRole;
	}

	@After
	public void closeGui() {
		pilot.close();
	}

}
