package ch.scaille.tcwriter.bpmn.flowable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import ch.scaille.tcwriter.bpmn.flowable.FlowableGenerator;
import ch.scaille.tcwriter.examples.ExampleFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FlowableGenerationTest {
    
    @Test
    void testGeneration() throws IOException {
        final var helper = new ExampleFactory();
        final var dictionary = helper.generateDictionary();

        final var outputFolder = Paths.get("./target/flowableModel");
        new FlowableGenerator(dictionary, outputFolder).generate();
        try (var fileList = Files.list(outputFolder)) {
            Assertions.assertEquals(26, fileList.count());
        }
    }
    
}
