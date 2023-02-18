package ch.scaille.tcwriter.examples;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

@SuppressWarnings("java:S2699")
class IntegrationTest {

    @Test
    void recordTest() throws IOException {
        final var exampleHelper = new ExampleHelper();
        final var model = exampleHelper.generateDictionary();
        final var testCase = exampleHelper.recordTestCase(model);
        assertNotEquals(0, testCase.getSteps().size());
    }

    @Test
    void testSerializeDeserialize() throws IOException {
        final var exampleHelper = new ExampleHelper();
        final var model = exampleHelper.generateDictionary();
        final var tc = exampleHelper.recordTestCase(model);

        exampleHelper.getModelDao().writeTestDictionary(model);
        exampleHelper.getModelDao().writeTestCase(ExampleHelper.TC_NAME, tc);

        // TODO: find a way to compare both
    }
}
