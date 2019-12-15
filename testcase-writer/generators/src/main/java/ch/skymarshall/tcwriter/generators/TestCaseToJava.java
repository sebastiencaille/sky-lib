package ch.skymarshall.tcwriter.generators;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.persistence.IModelPersister;
import ch.skymarshall.tcwriter.generators.model.persistence.JsonModelPersister;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.visitors.TestCaseToJunitVisitor;
import ch.skymarshall.util.generators.Template;

public class TestCaseToJava {

	private final Template testCaseTemplate;

	public TestCaseToJava(final GeneratorConfig config) throws IOException {
		testCaseTemplate = new Template(
				new String(Files.readAllBytes(Paths.get(config.getTemplatePath())), StandardCharsets.UTF_8));

	}

	public File generateAndWrite(final TestCase tc, final Path targetPath) throws IOException, TestCaseException {
		final Path targetFile = targetPath.resolve(tc.getPackageAndClassName().replace(".", "/") + ".java")
				.toAbsolutePath();
		Files.createDirectories(targetFile.getParent());
		Files.write(targetFile, generate(tc).getBytes(StandardCharsets.UTF_8));
		return targetFile.toAbsolutePath().toFile();
	}

	private String generate(final TestCase tc) throws IOException, TestCaseException {
		return new TestCaseToJunitVisitor(testCaseTemplate).visitTestCase(tc);
	}

	public static void main(final String[] args) throws IOException, TestCaseException {
		final IModelPersister persister = new JsonModelPersister();
		final GeneratorConfig config = persister.readConfiguration(args[0]);
		final String jsonTC = args[1];

		persister.setConfiguration(config);
		final TestModel testModel = persister.readTestModel();
		final TestCase tc = persister.readTestCase(jsonTC, testModel);

		new TestCaseToJava(config).generateAndWrite(tc, Paths.get(config.getDefaultGeneratedTCPath()));
	}

}
