package ch.skymarshall.tcwriter.generators;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.skymarshall.util.generators.Template;

import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;

public class TestCaseToJavaGenerator {

	private final Template testCaseTemplate;

	public TestCaseToJavaGenerator(final Path template) throws IOException {
		testCaseTemplate = new Template(new String(Files.readAllBytes(template), StandardCharsets.UTF_8));

	}

	public File generate(final TestCase tc, final Path targetPath) throws IOException, TestCaseException {
		final Path targetFile = targetPath.resolve(tc.getPath().replace(".", "/") + ".java");
		Files.write(targetFile,
				new JunitTestCaseVisitor(testCaseTemplate, true).visitTestCase(tc).getBytes(StandardCharsets.UTF_8));
		return targetFile.toFile();
	}
}
