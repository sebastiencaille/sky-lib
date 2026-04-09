package ch.scaille.tcwriter.flow.flowable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import ch.scaille.tcwriter.examples.ExampleHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FlowGenerationTest {
    
    @Test
    void testGeneration() throws IOException {
        final var helper = new ExampleHelper();
        final var dictionary = helper.generateDictionary();

        final var outputFolder = Paths.get("./target/flowableModel");
        new FlowGenerator(dictionary, outputFolder).generate();
        try (var fileList = Files.list(outputFolder)) {
            Assertions.assertEquals(26, fileList.count());
        }
    }
    
}
