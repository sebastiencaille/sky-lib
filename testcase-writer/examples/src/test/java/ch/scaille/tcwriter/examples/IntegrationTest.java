package ch.scaille.tcwriter.examples;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

@SuppressWarnings("java:S2699")
class IntegrationTest {

	@Test
	void recordTest()  {
		final var model = ExampleHelper.generateDictionary();
		final var testCase = ExampleHelper.recordTestCase(model);
		assertNotEquals(0, testCase.getSteps().size());
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
