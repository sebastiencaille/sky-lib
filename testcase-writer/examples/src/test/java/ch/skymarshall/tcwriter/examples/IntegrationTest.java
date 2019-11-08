package ch.skymarshall.tcwriter.examples;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import ch.skymarshall.tcwriter.generators.JsonHelper;
import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;

public class IntegrationTest {

	@Test
	public void generateModelAndTC() throws IOException, TestCaseException {
		final TestModel model = ExampleHelper.generateModel();
		final TestCase testCase = ExampleHelper.recordTestCase(model);
		ExampleHelper.saveModel(model);
		ExampleHelper.saveTC(testCase);
		ExampleHelper.generateCode(testCase);
	}

	@Test
	public void testSerializeDeserialize() throws IOException {
		final TestModel model = ExampleHelper.generateModel();
		final TestCase rc = ExampleHelper.recordTestCase(model);

		final File tmpModel = File.createTempFile("tc-model", ".json");
		tmpModel.deleteOnExit();
		final File tmpTC = File.createTempFile("tc-content", ".json");
		tmpTC.deleteOnExit();
		ExampleHelper.saveModel(tmpModel.toPath(), model);
		ExampleHelper.saveTC(tmpTC.toPath(), rc);

		final TestModel readModel = JsonHelper.testModelFromJson(JsonHelper.readFile(ExampleHelper.MODEL_PATH));
		final TestCase readTC = JsonHelper.testCaseFromJson(JsonHelper.readFile(ExampleHelper.TC_PATH), readModel);

		ExampleHelper.saveModel(tmpModel.toPath(), model);
		ExampleHelper.saveTC(tmpTC.toPath(), rc);

		// TODO: find a way to compare both
	}
}
