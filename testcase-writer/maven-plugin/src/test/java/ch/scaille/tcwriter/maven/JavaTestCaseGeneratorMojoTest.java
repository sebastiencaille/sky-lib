package ch.scaille.tcwriter.maven;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Files;

import java.nio.file.Paths;


import org.apache.maven.api.Project;


import org.apache.maven.api.di.Inject;
import org.apache.maven.testing.plugin.InjectMojo;
import org.apache.maven.testing.plugin.MojoParameter;
import org.apache.maven.testing.plugin.MojoTest;
import org.apache.maven.api.services.ProjectManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@MojoTest
class JavaTestCaseGeneratorMojoTest {

    private static final String SRC_TEST_RESOURCES_UNIT = "src/test/resources/unit";

    @Inject
    private Project project;
    @Inject
    private ProjectManager projectManager;

    @Test
    @InjectMojo(goal = "generateTestCases", pom = SRC_TEST_RESOURCES_UNIT + "/nominal.xml")
    @MojoParameter(name = "dictionaryFolder", value = SRC_TEST_RESOURCES_UNIT + "/dictionaries")
    @MojoParameter(name = "templatesFolder", value = SRC_TEST_RESOURCES_UNIT + "/templates")
    void testGeneration(JavaTestCaseGeneratorMojo myMojo) {
        assertNotNull(myMojo);
        myMojo.execute();
        Assertions.assertTrue(Files.exists(Paths.get("target/generated-test-sources/tcwriter/ch/scaille/tcwriter/examples/GeneratedTest.java")));
    }
}
