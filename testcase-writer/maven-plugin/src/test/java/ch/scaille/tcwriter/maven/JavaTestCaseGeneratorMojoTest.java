package ch.scaille.tcwriter.maven;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;

@MojoTest
class JavaTestCaseGeneratorMojoTest {

	@InjectMojo(goal = "generateTestCases", pom = "target/test-classes/nominal-test/")
	private JavaTestCaseGeneratorMojo javaTestCaseGeneratorMojo;
	
	void testGeneration() throws Exception {
		javaTestCaseGeneratorMojo.execute();

	}
}
