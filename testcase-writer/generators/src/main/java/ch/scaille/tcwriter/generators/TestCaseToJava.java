package ch.scaille.tcwriter.generators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.scaille.tcwriter.generators.model.TestCaseException;
import ch.scaille.tcwriter.generators.model.persistence.IModelPersister;
import ch.scaille.tcwriter.generators.model.persistence.JsonModelPersister;
import ch.scaille.tcwriter.generators.model.testapi.TestDictionary;
import ch.scaille.tcwriter.generators.model.testcase.TestCase;
import ch.scaille.tcwriter.generators.visitors.TestCaseToJunitVisitor;
import ch.scaille.util.generators.Template;

public class TestCaseToJava {

	private final Template testCaseTemplate;

	public TestCaseToJava(final IModelPersister persister) throws IOException {
		testCaseTemplate = persister.readTemplate();
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

		new TestCaseToJava(persister).generateAndWrite(tc, Paths.get(config.getDefaultGeneratedTCPath()));
	}

}
