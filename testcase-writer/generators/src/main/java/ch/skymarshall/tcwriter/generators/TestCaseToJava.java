package ch.skymarshall.tcwriter.generators;

import static ch.skymarshall.tcwriter.generators.JsonHelper.readFile;
import static ch.skymarshall.tcwriter.generators.JsonHelper.testCaseFromJson;
import static ch.skymarshall.tcwriter.generators.JsonHelper.testModelFromJson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.visitors.TestCaseToJunitVisitor;
import ch.skymarshall.util.generators.Template;

public class TestCaseToJava {

	private final Template testCaseTemplate;

	public TestCaseToJava(final Path template) throws IOException {
		testCaseTemplate = new Template(new String(Files.readAllBytes(template), StandardCharsets.UTF_8));

	}

	public File generateAndWrite(final TestCase tc, final Path targetPath) throws IOException, TestCaseException {
		final Path targetFile = targetPath.resolve(tc.getPackageAndClassName().replace(".", "/") + ".java");
		Files.write(targetFile, generate(tc).getBytes(StandardCharsets.UTF_8));
		return targetFile.toAbsolutePath().toFile();
	}

	private String generate(final TestCase tc) throws IOException, TestCaseException {
		return new TestCaseToJunitVisitor(testCaseTemplate).visitTestCase(tc);
	}

	public static void main(final String[] args) throws IOException, TestCaseException {

		final String jsonModel = args[0];
		final String jsonTC = args[1];
		final String javaTemplate = args[0];
		final String javaTargetPath = args[2];

		final TestModel testModel = testModelFromJson(readFile(new File(jsonModel).toPath()));
		final TestCase tc = testCaseFromJson(readFile(new File(jsonTC).toPath()), testModel);

		new TestCaseToJava(new File(javaTemplate).toPath()).generateAndWrite(tc, new File(javaTargetPath).toPath());
	}

}
