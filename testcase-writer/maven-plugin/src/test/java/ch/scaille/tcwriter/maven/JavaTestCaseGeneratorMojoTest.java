package ch.scaille.tcwriter.maven;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.apache.maven.api.di.Provides;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoParameter;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.model.Build;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@MojoTest
class JavaTestCaseGeneratorMojoTest {

    private static final String SRC_TEST_RESOURCES_UNIT = "src/test/resources/unit";
    private static final String BUILD_DIR = "target";

    @Provides
    private MavenProject project() {
        final var mock = mock(MavenProject.class);
        final var unitTestResource = new Resource();
        unitTestResource.setDirectory(SRC_TEST_RESOURCES_UNIT);
        Mockito.when(mock.getTestResources()).thenReturn(List.of(unitTestResource));
        final var mockBuild = Mockito.mock(Build.class);
        Mockito.when(mockBuild.getDirectory()).thenReturn(BUILD_DIR);
        Mockito.when(mock.getBuild()).thenReturn(mockBuild);
        return mock;
    }

    @Test
    @InjectMojo(goal = "generateTestCases", pom = SRC_TEST_RESOURCES_UNIT + "/nominal.xml")
    @MojoParameter(name = "dictionaryFolder", value = SRC_TEST_RESOURCES_UNIT + "/dictionaries")
    @MojoParameter(name = "template", value = SRC_TEST_RESOURCES_UNIT + "/templates/TC.template")
    void testGeneration(JavaTestCaseGeneratorMojo myMojo) throws Exception {
        assertNotNull(myMojo);
        myMojo.execute();
        Assertions.assertTrue(Files.exists(Paths.get("target/generated-test-sources/tcwriter/ch/scaille/tcwriter/examples/GeneratedTest.java")));
    }
}
