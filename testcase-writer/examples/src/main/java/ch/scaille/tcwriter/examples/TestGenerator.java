package ch.scaille.tcwriter.examples;

import static ch.scaille.util.helpers.LambdaExt.uncheckF;

import java.io.IOException;

import ch.scaille.tcwriter.generators.TestCaseToJava;
import ch.scaille.tcwriter.model.TestCaseException;

public class TestGenerator {

	public static void main(String[] args) throws IOException, TestCaseException {

		final var model = ExampleHelper.generateDictionary();
		final var testCase = ExampleHelper.recordTestCase(model);

		ExampleHelper.getModelDao().writeTestDictionary(model);
		ExampleHelper.getModelDao().writeTestCase(ExampleHelper.TC_NAME, testCase);
		new TestCaseToJava(ExampleHelper.getModelDao()).generate(testCase)
				.writeTo(uncheckF(ExampleHelper.getModelDao()::exportTestCase));
	}
}
