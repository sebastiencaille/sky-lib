package ch.skymarshall.tcwriter.examples;

import static ch.skymarshall.tcwriter.examples.ExampleHelper.getConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

import ch.skymarshall.tcwriter.generators.TestCaseToJava;
import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;

public class IntegrationTest {

	@Test
	public void generateModelAndTC() throws IOException, TestCaseException {
		final TestModel model = ExampleHelper.generateModel();
		final TestCase testCase = ExampleHelper.recordTestCase(model);
		ExampleHelper.saveModel(model);
		ExampleHelper.saveTC(ExampleHelper.TC_NAME, testCase);
		new TestCaseToJava(getConfig()).generateAndWrite(testCase, Paths.get(getConfig().getDefaultGeneratedTCPath()));
	}

	@Test
	public void testSerializeDeserialize() throws IOException {
		final TestModel model = ExampleHelper.generateModel();
		final TestCase tc = ExampleHelper.recordTestCase(model);

		final File tmpModel = File.createTempFile("tc-model", ".json");
		tmpModel.deleteOnExit();
		final File tmpTC = File.createTempFile("tc-content", ".json");
		tmpTC.deleteOnExit();
		ExampleHelper.getPersister().writeTestModel(tmpModel.toPath(), model);
		ExampleHelper.saveTC(ExampleHelper.TC_NAME + "-tmp", tc);

		// TODO: find a way to compare both
	}
}
