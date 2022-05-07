package ch.scaille.tcwriter.examples;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

@SuppressWarnings("java:S2699")
class IntegrationTest {

	@Test
	void recordTest() {
		final var model = ExampleHelper.generateDictionary();
		final var testCase = ExampleHelper.recordTestCase(model);
		assertNotEquals(0, testCase.getSteps().size());
	}

	@Test
	void testSerializeDeserialize() throws IOException {
		final var model = ExampleHelper.generateDictionary();
		final var tc = ExampleHelper.recordTestCase(model);

		ExampleHelper.getModelDao().writeTestDictionary(model);
		ExampleHelper.getModelDao().writeTestCase(ExampleHelper.TC_NAME, tc);

		// TODO: find a way to compare both
	}
}
