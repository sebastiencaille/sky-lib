package ch.scaille.tcwriter.examples.tests;

import java.io.IOException;

import ch.scaille.generators.util.GenerationMetadata;
import ch.scaille.tcwriter.examples.ExampleFactory;
import ch.scaille.tcwriter.javatc.visitors.TestCaseToJavaVisitor;
import ch.scaille.tcwriter.model.TestCaseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class IntegrationTest {

    @Test
    void recordAndGenerateTest() throws IOException {
        final var exampleHelper = new ExampleFactory();
        exampleHelper.getConfigDao().saveConfiguration();

        final var model = exampleHelper.generateDictionary();

        // Record test
        final var testCase = exampleHelper.recordTestCase(model);
        assertNotEquals(0, testCase.getSteps().size());

        final var modelDao = exampleHelper.getModelDao();
        modelDao.writeTestDictionary(model);
        modelDao.writeTestCase(ExampleFactory.TC_FILE_YAML, testCase);
    }

    @Test
    void testSerializeDeserialize() throws IOException, TestCaseException {
        final var exampleHelper = new ExampleFactory();
        final var generatedDictionary = exampleHelper.generateDictionary();
        final var generatedTc = exampleHelper.recordTestCase(generatedDictionary);

        exampleHelper.getModelDao().writeTestDictionary(generatedDictionary);
        exampleHelper.getModelDao().writeTestCase(ExampleFactory.TC_FILE_YAML, generatedTc);

        final var testDictionary = exampleHelper.getModelDao().readTestDictionary("SimpleTest");
        assertTrue(testDictionary.isPresent());
        assertNotNull(testDictionary.get().getActors().values().iterator().next().getRole());
        final var loadedTC = exampleHelper.getModelDao().readTestCase(ExampleFactory.TC_FILE_YAML, testDictionary.get());

        assertTrue(loadedTC.isPresent());
        new TestCaseToJavaVisitor(exampleHelper.getModelDao().readTemplate("SimpleTest-java"))
                .visitTestCase(loadedTC.get(), new GenerationMetadata(IntegrationTest.class, ""));
    }
}
