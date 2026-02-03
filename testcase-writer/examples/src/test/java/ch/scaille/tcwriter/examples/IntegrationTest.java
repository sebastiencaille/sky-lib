package ch.scaille.tcwriter.examples;

import java.io.IOException;

import ch.scaille.generators.util.GenerationMetadata;
import ch.scaille.tcwriter.generators.services.visitors.TestCaseToJavaVisitor;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.testcase.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        modelDao.writeTestCase(ExampleHelper.TC_FILE_YAML, testCase);
    }

    @Test
    void testSerializeDeserialize() throws IOException, TestCaseException {
        final var exampleHelper = new ExampleHelper();
        final var generatedDictionary = exampleHelper.generateDictionary();
        final var generatedTc = exampleHelper.recordTestCase(generatedDictionary);

        exampleHelper.getModelDao().writeTestDictionary(generatedDictionary);
        exampleHelper.getModelDao().writeTestCase(ExampleHelper.TC_FILE_YAML, generatedTc);

        final var testDictionary = exampleHelper.getModelDao().readTestDictionary("SimpleTest");
        assertTrue(testDictionary.isPresent());
        assertNotNull(testDictionary.get().getActors().values().iterator().next().getRole());
        final var loadedTC = exampleHelper.getModelDao().readTestCase(ExampleHelper.TC_FILE_YAML, testDictionary.get());

        assertTrue(loadedTC.isPresent());
        new TestCaseToJavaVisitor(exampleHelper.getModelDao().readTemplate("SimpleTest-java"))
                .visitTestCase(loadedTC.get(), new GenerationMetadata(IntegrationTest.class, ""));
    }
}
