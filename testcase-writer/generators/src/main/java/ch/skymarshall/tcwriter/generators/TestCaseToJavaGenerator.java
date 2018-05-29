package ch.skymarshall.tcwriter.generators;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.skymarshall.util.generators.Template;

import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestCaseException;

public class TestCaseToJavaGenerator {

	private final Template testCaseTemplate;

	public TestCaseToJavaGenerator(final Path template) throws IOException {
		testCaseTemplate = new Template(new String(Files.readAllBytes(template), StandardCharsets.UTF_8));

	}

	public void generate(final TestCase tc, final Path targetPath) throws IOException, TestCaseException {
		Files.write(targetPath.resolve(tc.getPath().replace(".", "/") + ".java"),
				new JavaGenerationVisitor(testCaseTemplate).visitTestCase(tc).getBytes(StandardCharsets.UTF_8));
	}
}
