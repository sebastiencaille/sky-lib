package ch.scaille.tcwriter.bpmn.camunda;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import ch.scaille.tcwriter.examples.ExampleFactory;
import org.apache.velocity.app.VelocityEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CamundaGenerationTest {
    
    @Test
    void testGeneration() throws IOException {
        final var helper = new ExampleFactory();
        final var dictionary = helper.generateDictionary();

        final var outputFolder = Paths.get("./target/camundaModel");
        new CamundaGenerator(dictionary, outputFolder) {
            @Override
            public VelocityEngine generate() throws IOException {
                final var engine = super.generate();
                generateFile(engine, Map.of(), "tcw-tc.bpmn", "tcw-tc.bpmn");
                return engine;
            }
        }.generate();
        try (var fileList = Files.list(outputFolder)) {
            Assertions.assertEquals(17, fileList.count());
        }
    }
    
}
