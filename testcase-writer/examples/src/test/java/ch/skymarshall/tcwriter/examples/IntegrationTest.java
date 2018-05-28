package ch.skymarshall.tcwriter.examples;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import ch.skymarshall.tcwriter.examples.hmi.ExampleTCWriter;
import ch.skymarshall.tcwriter.generators.TestCaseToJavaGenerator;
import ch.skymarshall.tcwriter.generators.model.TestCase;

public class IntegrationTest {

	@Test
	public void generateModelAndTC() throws IOException {
		final TestCase testCast = ExampleTCWriter.createTestCase();
		new TestCaseToJavaGenerator(new File("./src/main/resources/templates/TC.template").toPath()).generate(testCast,
				new File("./src/test/java").toPath());
	}
}
