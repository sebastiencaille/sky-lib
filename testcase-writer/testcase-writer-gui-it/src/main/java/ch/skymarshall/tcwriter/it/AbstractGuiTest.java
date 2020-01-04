package ch.skymarshall.tcwriter.it;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.junit.After;

import ch.skymarshall.tcwriter.generators.GeneratorConfig;
import ch.skymarshall.tcwriter.generators.JavaToModel;
import ch.skymarshall.tcwriter.generators.model.persistence.JsonModelPersister;
import ch.skymarshall.tcwriter.generators.model.testapi.TestActor;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.gui.frame.TCWriterController;
import ch.skymarshall.util.helpers.ClassLoaderHelper;
import executors.ITestExecutor;
import executors.JunitTestExecutor;

public class AbstractGuiTest {

	private static final File RESOURCE_FOLDER = new File("./src/main/resources");
	protected LocalTCWriterRole tcWriter;
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
		config.setModelPath(modelPath + "/test-model.json");
		config.setTemplatePath(new File("./src/main/resources/templates/TC.template").toString());
		final JsonModelPersister persister = new JsonModelPersister(config);

		final TestModel model = new JavaToModel(asList(TestWriterRole.class)).generateModel();
		model.addActor(new TestActor("TestCase writer", "tcWriter", model.getRole(TestWriterRole.class)), null);
		persister.writeTestModel(model);

		final ITestExecutor executor = new JunitTestExecutor(config, ClassLoaderHelper.appClassPath());

		final TCWriterController controller = new TCWriterController(config, persister, executor);
		SwingUtilities.invokeAndWait(() -> controller.run());
		pilot = new TCGuiPilot(controller.getGui());

		tcWriter = new LocalTCWriterRole(pilot);
	}

	@After
	public void closeGui() {
		pilot.close();
	}

}
