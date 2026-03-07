package ch.scaille.tcwriter.flow.flowable;

import java.io.IOException;
import java.nio.file.Paths;

import ch.scaille.tcwriter.examples.ExampleHelper;
import org.junit.jupiter.api.Test;

class FlowGenerationTest {
    
    @Test
    void testGeneration() throws IOException {
        final var helper = new ExampleHelper();
        final var dictionary = helper.generateDictionary();

        new FlowGenerator(dictionary, Paths.get("./target/flowableModel")).generate();
    }
    
}
