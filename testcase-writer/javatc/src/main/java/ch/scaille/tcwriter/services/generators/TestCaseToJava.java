package ch.scaille.tcwriter.services.generators;

import static ch.scaille.tcwriter.persistence.factory.DaoConfigs.homeFolder;
import static ch.scaille.util.helpers.LambdaExt.uncheckedC;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ch.scaille.generators.util.GenerationMetadata;
import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.generators.services.visitors.TestCaseToJunitVisitor;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.tcwriter.persistence.factory.DaoConfigs;

public class TestCaseToJava {

	public static class Args {
		@Parameter(names = { "-c" }, description = "Name of configuration")
		public String configuration;

		@Parameter(names = { "-td" }, description = "Name of test case dictionary")
		public String tcDictionary = "default";

		@Parameter(names = { "-tc" }, required = true, description = "Name of test case")
		public String testCase;
	}

	public TestCaseToJava(IModelDao modelDao) {
		this.modelDao = modelDao;
	}

	private final IModelDao modelDao;

	public Template generate(TestCase tc, GenerationMetadata generationMetadata) throws TestCaseException {
		return new TestCaseToJunitVisitor(this.modelDao.readTemplate()).visitTestCase(tc, generationMetadata);
	}

	public static void main(String[] args) throws IOException, TestCaseException {
		final var generationMetadata = GenerationMetadata.fromCommandLine(TestCaseToJava.class, args);
		final var mainArgs = new Args();
		JCommander.newBuilder().addObject(mainArgs).build().parse(args);
		
		final var tcFile = mainArgs.testCase;

		final var daoConfig = DaoConfigs.withFolder(homeFolder());
		final var modelDao = daoConfig.modelDao();
		final var testDictionary = modelDao.readTestDictionary(mainArgs.tcDictionary)
				.orElseThrow(FileNotFoundException::new);
		final var testCase = modelDao.readTestCase(tcFile, preferred -> testDictionary).orElseThrow(FileNotFoundException::new);
		
		new TestCaseToJava(modelDao).generate(testCase, generationMetadata)
				.writeTo(uncheckedC(tc -> modelDao.writeTestCaseCode(tcFile, tc)));
	}

}
