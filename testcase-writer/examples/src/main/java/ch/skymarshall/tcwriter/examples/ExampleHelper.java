package ch.skymarshall.tcwriter.examples;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.skymarshall.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.skymarshall.tcwriter.generators.GeneratorConfig;
import ch.skymarshall.tcwriter.generators.JavaToModel;
import ch.skymarshall.tcwriter.generators.model.persistence.IModelPersister;
import ch.skymarshall.tcwriter.generators.model.persistence.JsonModelPersister;
import ch.skymarshall.tcwriter.generators.model.testapi.TestActor;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.recorder.TestCaseRecorder;
import ch.skymarshall.tcwriter.recording.TestCaseRecorderAspect;
import ch.skymarshall.util.helpers.ClassLoaderHelper;
import executors.ITestExecutor;
import executors.JunitTestExecutor;

public class ExampleHelper {

	private ExampleHelper() {
	}

	public static final File RESOURCE_FOLDER = new File("./src/main/resources");

	public static Path SRC_PATH = Paths.get("./src/test/java");

	public static String TC_NAME = "testCase.json";

	private static GeneratorConfig config;
	private static IModelPersister persister;

	static {

		config = new GeneratorConfig();
		final File tcPath = new File(RESOURCE_FOLDER, "testCase");
		tcPath.mkdirs();
		final File modelPath = new File(RESOURCE_FOLDER, "models");
		modelPath.mkdirs();

		config.setTcPath(tcPath.toString());
		config.setDefaultGeneratedTCPath("./src/tests/java");
		config.setModelPath(modelPath + "/test-model.json");
		config.setTemplatePath(new File("./src/main/resources/templates/TC.template").toString());
		persister = new JsonModelPersister(config);
	}

	public static IModelPersister getPersister() {
		return persister;
	}

	public static TestModel generateModel() {
		final TestModel model = new JavaToModel(asList(CustomerTestRole.class, DeliveryTestRole.class)).generateModel();
		model.addActor(new TestActor("customer", "customer", model.getRole(CustomerTestRole.class)), null);
		model.addActor(new TestActor("deliveryGuy", "deliveryGuy", model.getRole(DeliveryTestRole.class)), null);
		return model;
	}

	public static void saveModel(final TestModel model) throws IOException {
		persister.writeTestModel(model);
	}

	public static void saveTC(final String name, final TestCase testCase) throws IOException {
		persister.writeTestCase(name, testCase);
	}

	public static TestCase recordTestCase(final TestModel model) {
		final TestCaseRecorder recorder = new TestCaseRecorder(persister, model);
		TestCaseRecorderAspect.setRecorder(recorder);
		final ExampleTest test = new ExampleTest();
		test.initActors();
		test.testNormalCase();
		return recorder.getTestCase("ch.skymarshall.tcwriter.examples.GeneratedTest");
	}

	public static ITestExecutor testExecutor() throws IOException {
		return new JunitTestExecutor(config, ClassLoaderHelper.appClassPath());
	}

	public static GeneratorConfig getConfig() {
		return config;
	}

}
