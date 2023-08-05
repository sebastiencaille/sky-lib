package ch.scaille.tcwriter.examples;

import static ch.scaille.util.helpers.LambdaExt.uncheckC;

import java.io.IOException;

import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.services.generators.TestCaseToJava;

public class JavaTestGenerator {

    public static void main(String[] args) throws IOException, TestCaseException {
        final var exampleHelper = new ExampleHelper();
        final var model = exampleHelper.generateDictionary();
        final var testCase = exampleHelper.recordTestCase(model);

        final var modelDao = exampleHelper.getModelDao();
        modelDao.writeTestDictionary(model);
        modelDao.writeTestCase(ExampleHelper.TC_NAME, testCase);
        new TestCaseToJava(modelDao).generate(testCase).writeTo(uncheckC(tc -> modelDao.writeTestCaseCode(ExampleHelper.TC_NAME, tc)));
    }
}
