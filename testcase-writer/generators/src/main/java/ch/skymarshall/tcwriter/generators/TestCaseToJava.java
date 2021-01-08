package ch.skymarshall.tcwriter.generators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.persistence.IModelPersister;
import ch.skymarshall.tcwriter.generators.model.persistence.JsonModelPersister;
import ch.skymarshall.tcwriter.generators.model.testapi.TestDictionary;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.visitors.TestCaseToJunitVisitor;
import ch.skymarshall.util.generators.Template;

public class TestCaseToJava {

	private final Template testCaseTemplate;

	public TestCaseToJava(final GeneratorConfig config) throws IOException {
		testCaseTemplate = Template.from(config.getTemplatePath());
	}

	public File generateAndWrite(final TestCase tc, final Path targetPath) throws IOException, TestCaseException {
		return new TestCaseToJunitVisitor(testCaseTemplate).visitTestCase(tc).writeToFolder(targetPath.toFile());
	}

	public static void main(final String[] args) throws IOException, TestCaseException {
		final IModelPersister persister = new JsonModelPersister();
		final GeneratorConfig config = persister.readConfiguration(args[0]);
		persister.setConfiguration(config);
		
		final TestDictionary testDictionary = persister.readTestDictionary();
		final String jsonTC = args[1];
		final TestCase tc = persister.readTestCase(jsonTC, testDictionary);

		new TestCaseToJava(config).generateAndWrite(tc, Paths.get(config.getDefaultGeneratedTCPath()));
	}

}
