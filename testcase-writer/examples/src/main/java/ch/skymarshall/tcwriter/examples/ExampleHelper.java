package ch.skymarshall.tcwriter.examples;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import ch.skymarshall.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.skymarshall.tcwriter.generators.JavaToModel;
import ch.skymarshall.tcwriter.generators.JsonHelper;
import ch.skymarshall.tcwriter.generators.model.testapi.TestActor;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.recorder.TestCaseRecorder;
import ch.skymarshall.tcwriter.recording.TestCaseRecorderAspect;
import executors.ITestExecutor;
import executors.JunitTestExecutor;

public class ExampleHelper {

	private ExampleHelper() {
	}

	public static final File RESOURCE_FOLDER = new File("src/main/resources/models");

	public static Path MODEL_PATH = new File(RESOURCE_FOLDER, "testModel.json").toPath();

	public static Path TC_PATH = new File(RESOURCE_FOLDER, "testCase.json").toPath();

	public static Path TEMPLATE_PATH = new File("./src/main/resources/templates/TC.template").toPath();

	public static Path SRC_PATH = new File("./src/test/java").toPath();

	public static TestModel generateModel() {
		final TestModel model = new JavaToModel(asList(CustomerTestRole.class, DeliveryTestRole.class)).generateModel();
		model.addActor(new TestActor("customer", "customer", model.getRole(CustomerTestRole.class)), null);
		model.addActor(new TestActor("deliveryGuy", "deliveryGuy", model.getRole(DeliveryTestRole.class)), null);
		return model;
	}

	public static void saveModel(final TestModel model) throws IOException {
		final Path modelPath = ExampleHelper.MODEL_PATH;
		saveModel(modelPath, model);
	}

	public static void saveModel(final Path modelPath, final TestModel model) throws IOException {
		final String jsonModel = JsonHelper.toJson(model);
		Files.write(modelPath, jsonModel.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}

	public static void saveTC(final TestCase testCase) throws IOException {
		saveTC(ExampleHelper.TC_PATH, testCase);
	}

	public static void saveTC(final Path tcPath, final TestCase testCase) throws IOException {
		final String jsonTestCase = JsonHelper.toJson(testCase);
		Files.write(tcPath, jsonTestCase.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}

	public static TestCase recordTestCase(final TestModel model) {
		final TestCaseRecorder recorder = new TestCaseRecorder(model);
		TestCaseRecorderAspect.setRecorder(recorder);
		final ExampleTest test = new ExampleTest();
		test.initActors();
		test.testNormalCase();
		return recorder.getTestCase("ch.skymarshall.tcwriter.examples.GeneratedTest");
	}

	public static ITestExecutor testExecutor() throws IOException {
		return new JunitTestExecutor(ExampleHelper.TEMPLATE_PATH, ExampleHelper.SRC_PATH);
	}

}
