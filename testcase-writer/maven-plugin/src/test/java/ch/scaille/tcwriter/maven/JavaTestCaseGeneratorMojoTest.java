package ch.scaille.tcwriter.maven;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

public class JavaTestCaseGeneratorMojoTest {
	@Rule
	public final MojoRule rule = new MojoRule() {
		@Override
		protected void before() {
			// noop
		}

		@Override
		protected void after() {
			// noop
		}
	};

	/**
	 * @throws Exception if any
	 */
	@Test
	public void testGeneration() throws Exception {
		File pom = new File("target/test-classes/nominal-test/");
		assertNotNull(pom);
		assertTrue(pom.exists());

		JavaTestCaseGeneratorMojo myMojo = rule.lookupConfiguredMojo(pom,
				"generateTestCases");
		assertNotNull(myMojo);
		myMojo.execute();

	}
}
