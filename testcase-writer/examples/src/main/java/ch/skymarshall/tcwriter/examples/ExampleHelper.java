package ch.skymarshall.tcwriter.examples;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;

import ch.skymarshall.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.skymarshall.tcwriter.executors.ITestExecutor;
import ch.skymarshall.tcwriter.executors.JunitTestExecutor;
import ch.skymarshall.tcwriter.generators.GeneratorConfig;
import ch.skymarshall.tcwriter.generators.JavaToDictionary;
import ch.skymarshall.tcwriter.generators.model.persistence.IModelPersister;
import ch.skymarshall.tcwriter.generators.model.persistence.JsonModelPersister;
import ch.skymarshall.tcwriter.generators.model.testapi.TestActor;
import ch.skymarshall.tcwriter.generators.model.testapi.TestDictionary;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.recorder.TestCaseRecorder;
import ch.skymarshall.tcwriter.generators.visitors.HumanReadableVisitor;
import ch.skymarshall.tcwriter.recording.TestCaseRecorderAspect;
import ch.skymarshall.util.helpers.ClassLoaderHelper;

public class ExampleHelper {

	private ExampleHelper() {
	}

	public static final File RESOURCE_FOLDER = new File("./src/main/resources");

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
		config.setDefaultGeneratedTCPath("./src/test/java");
		config.setDictionaryPath(modelPath + "/test-model.json");
		config.setTemplatePath(new File("./src/main/resources/templates/TC.template").toString());
		persister = new JsonModelPersister(config);
	}

	public static IModelPersister getPersister() {
		return persister;
	}

	public static TestDictionary generateDictionary() {
		final TestDictionary dictionary = new JavaToDictionary(asList(CustomerTestRole.class, DeliveryTestRole.class))
				.generateDictionary();
		dictionary.addActor(new TestActor("customer", "customer", dictionary.getRole(CustomerTestRole.class)), null);
		dictionary.addActor(new TestActor("deliveryGuy", "deliveryGuy", dictionary.getRole(DeliveryTestRole.class)),
				null);
		return dictionary;
	}

	public static void saveDictionary(final TestDictionary dictionary) throws IOException {
		persister.writeTestDictionary(dictionary);
	}

	public static void saveTC(final String name, final TestCase testCase) throws IOException {
		persister.writeTestCase(name, testCase);
	}

	public static TestCase recordTestCase(final TestDictionary model) {
		final TestCaseRecorder recorder = new TestCaseRecorder(persister, model);
		TestCaseRecorderAspect.setRecorder(recorder);
		final SimpleTest test = new SimpleTest();
		test.initActors();
		test.testNormalCase();
		final TestCase testCase = recorder.getTestCase("ch.skymarshall.tcwriter.examples.GeneratedTest");
		System.out.println(new HumanReadableVisitor(testCase, true).processAllSteps());
		return testCase;
	}

	public static ITestExecutor testExecutor() throws IOException {
		return new JunitTestExecutor(config, ClassLoaderHelper.appClassPath());
	}

	public static GeneratorConfig getConfig() {
		return config;
	}

}
