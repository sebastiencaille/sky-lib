package ch.scaille.tcwriter.examples;

import static ch.scaille.util.helpers.LambdaExt.uncheckF;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.scaille.tcwriter.generators.TestCaseToJava;
import ch.scaille.tcwriter.generators.model.TestCaseException;

@SuppressWarnings("java:S2699")
class IntegrationTest {

	@Test
	void generateDictionaryAndTC() throws IOException, TestCaseException {
		final var model = ExampleHelper.generateDictionary();
		final var testCase = ExampleHelper.recordTestCase(model);
		assertNotEquals(0, testCase.getSteps().size());

		ExampleHelper.saveDictionary(model);
		ExampleHelper.saveTC(ExampleHelper.TC_NAME, testCase);
		new TestCaseToJava(ExampleHelper.getModelDao()).generate(testCase)
				.writeTo(uncheckF(ExampleHelper.getModelDao()::exportTestCase));
	}

	@Test
	void testSerializeDeserialize() throws IOException {
		final var model = ExampleHelper.generateDictionary();
		final var tc = ExampleHelper.recordTestCase(model);

		final var tmpModel = File.createTempFile("tc-model", ".json");
		tmpModel.deleteOnExit();
		final var tmpTC = File.createTempFile("tc-content", ".json");
		tmpTC.deleteOnExit();
		ExampleHelper.getModelDao().writeTestDictionary(tmpModel.toPath(), model);
		ExampleHelper.saveTC(ExampleHelper.TC_NAME + "-tmp", tc);

		// TODO: find a way to compare both
	}
}
