package ch.scaille.tcwriter.examples;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class IntegrationTest {

    @Test
    void recordAndGenerateTest() throws IOException {
        final var exampleHelper = new ExampleHelper();
        exampleHelper.getConfigDao().saveConfiguration();

        final var model = exampleHelper.generateDictionary();
        
        // Record test
        final var testCase = exampleHelper.recordTestCase(model);
        assertNotEquals(0, testCase.getSteps().size());
        
        final var modelDao = exampleHelper.getModelDao();
        modelDao.writeTestDictionary(model);
        modelDao.writeTestCase(ExampleHelper.TC_FILE, testCase);
        
    }

    @Test
    void testSerializeDeserialize() throws IOException {
        final var exampleHelper = new ExampleHelper();
        final var model = exampleHelper.generateDictionary();
        final var tc = exampleHelper.recordTestCase(model);

        exampleHelper.getModelDao().writeTestDictionary(model);
        exampleHelper.getModelDao().writeTestCase(ExampleHelper.TC_FILE, tc);

        // TODO: find a way to compare both
    }
}
