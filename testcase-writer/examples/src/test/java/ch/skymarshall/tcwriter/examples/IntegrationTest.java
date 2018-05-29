package ch.skymarshall.tcwriter.examples;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import ch.skymarshall.tcwriter.examples.hmi.ExampleTCWriter;
import ch.skymarshall.tcwriter.generators.TestCaseToJavaGenerator;
import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestCaseException;

public class IntegrationTest {

	@Test
	public void generateModelAndTC() throws IOException, TestCaseException {
		final TestCase testCast = ExampleTCWriter.createTestCase();
		new TestCaseToJavaGenerator(new File("./src/main/resources/templates/TC.template").toPath()).generate(testCast,
				new File("./src/test/java").toPath());
	}
}
