package ch.scaille.tcwriter.generators;

import static ch.scaille.util.helpers.LambdaExt.uncheckF2;

import java.io.IOException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.generators.visitors.TestCaseToJunitVisitor;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.persistence.FsModelDao;
import ch.scaille.tcwriter.model.persistence.IModelDao;
import ch.scaille.tcwriter.model.testcase.TestCase;

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

	public Template generate(TestCase tc) throws IOException, TestCaseException {
		return new TestCaseToJunitVisitor(this.modelDao.readTemplate()).visitTestCase(tc);
	}

	public static void main(String[] args) throws IOException, TestCaseException {
		var mainArgs = new Args();
		JCommander.newBuilder().addObject(mainArgs).build().parse(args);
		var modelDao = new FsModelDao(FsModelDao.loadConfiguration(mainArgs.configuration));
		var testDictionary = modelDao.readTestDictionary(mainArgs.tcDictionary);
		var jsonTC = mainArgs.testCase;
		var testcase = modelDao.readTestCase(jsonTC, testDictionary);
		new TestCaseToJava(modelDao).generate(testcase).writeTo(uncheckF2(modelDao::exportTestCase));
	}

}
