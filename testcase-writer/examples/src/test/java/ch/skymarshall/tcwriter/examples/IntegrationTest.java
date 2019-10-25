package ch.skymarshall.tcwriter.examples;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

import ch.skymarshall.tcwriter.examples.gui.ExampleTCWriter;
import ch.skymarshall.tcwriter.generators.TestCaseToJava;
import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.visitors.JsonHelper;

public class IntegrationTest {

	@Test
	public void generateModelAndTC() throws IOException, TestCaseException {
		final TestCase testCase = ExampleTCWriter.createTestCase();
		new TestCaseToJava(new File("./src/main/resources/templates/TC.template").toPath()).generateAndWrite(testCase,
				new File("./src/test/java").toPath());
	}

	@Test
	public void testSerializeDeserialize() throws IOException {
		final File tmp = new File(System.getProperty("java.io.tmpdir"));

		final Path modelPath = new File(tmp, "testModel.json").toPath();
		final Path testCasePath = new File(tmp, "testCase.json").toPath();

		final TestCase testCase = ExampleTCWriter.createTestCase();
		final String jsonModel = JsonHelper.toJson(testCase.getModel());
		Files.write(modelPath, jsonModel.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);

		final String jsonTestCase = JsonHelper.toJson(testCase);
		Files.write(testCasePath, jsonTestCase.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);

		final TestModel testModel2 = JsonHelper.testModelFromJson(Files.readAllLines(modelPath).get(0));
		final TestCase testCase2 = JsonHelper.testCaseFromJson(Files.readAllLines(testCasePath).get(0), testModel2);

	}
}
