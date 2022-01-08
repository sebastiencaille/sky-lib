package ch.scaille.tcwriter.examples;

import static ch.scaille.tcwriter.examples.ExampleHelper.getConfig;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import ch.scaille.tcwriter.generators.TestCaseToJava;
import ch.scaille.tcwriter.generators.model.TestCaseException;
import ch.scaille.tcwriter.generators.model.testapi.TestDictionary;
import ch.scaille.tcwriter.generators.model.testcase.TestCase;

@SuppressWarnings("java:S2699")
class IntegrationTest {

	@Test
	void generateDictionaryAndTC() throws IOException, TestCaseException {
		final TestDictionary model = ExampleHelper.generateDictionary();
		final TestCase testCase = ExampleHelper.recordTestCase(model);
		assertNotEquals(0, testCase.getSteps().size());

		ExampleHelper.saveDictionary(model);
		ExampleHelper.saveTC(ExampleHelper.TC_NAME, testCase);
		new TestCaseToJava(ExampleHelper.getPersister()).generateAndWrite(testCase,
				Paths.get(getConfig().getDefaultGeneratedTCPath()));
	}

	@Test
	void testSerializeDeserialize() throws IOException {
		final TestDictionary model = ExampleHelper.generateDictionary();
		final TestCase tc = ExampleHelper.recordTestCase(model);

		final File tmpModel = File.createTempFile("tc-model", ".json");
		tmpModel.deleteOnExit();
		final File tmpTC = File.createTempFile("tc-content", ".json");
		tmpTC.deleteOnExit();
		ExampleHelper.getPersister().writeTestDictionary(tmpModel.toPath(), model);
		ExampleHelper.saveTC(ExampleHelper.TC_NAME + "-tmp", tc);

		// TODO: find a way to compare both
	}
}
