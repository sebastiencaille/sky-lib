package ch.scaille.tcwriter.services.generators;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.generators.services.visitors.TestCaseToJunitVisitor;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.tcwriter.persistence.fsconfig.FsConfigDao;
import ch.scaille.tcwriter.persistence.fsmodel.FsModelDao;
import ch.scaille.util.helpers.LambdaExt;

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

	public Template generate(TestCase tc) throws TestCaseException {
		return new TestCaseToJunitVisitor(this.modelDao.readTemplate()).visitTestCase(tc);
	}

	public static void main(String[] args) throws IOException, TestCaseException {
		final var mainArgs = new Args();
		JCommander.newBuilder().addObject(mainArgs).build().parse(args);
		final var modelDao = new FsModelDao(FsConfigDao.localUser().setConfiguration(mainArgs.configuration));
		final var testDictionary = modelDao.readTestDictionary(mainArgs.tcDictionary).orElseThrow(FileNotFoundException::new);
		final var jsonTC = mainArgs.testCase;
		final var testcase = modelDao.readTestCase(jsonTC, testDictionary).orElseThrow(FileNotFoundException::new);
		new TestCaseToJava(modelDao).generate(testcase).writeTo(LambdaExt.uncheckC(tc-> modelDao.writeTestCaseCode(jsonTC, tc)));
	}

}