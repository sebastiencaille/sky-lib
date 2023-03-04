package ch.scaille.tcwriter.examples;

import static ch.scaille.util.helpers.LambdaExt.uncheckF;

import java.io.IOException;

import ch.scaille.tcwriter.generators.TestCaseToJava;
import ch.scaille.tcwriter.model.TestCaseException;

public class JavaTestGenerator {

    public static void main(String[] args) throws IOException, TestCaseException {
        final var exampleHelper = new ExampleHelper();
        final var model = exampleHelper.generateDictionary();
        final var testCase = exampleHelper.recordTestCase(model);

        final var modelDao = exampleHelper.getModelDao();
        modelDao.writeTestDictionary(model);
        modelDao.writeTestCase(ExampleHelper.TC_NAME, testCase);
        new TestCaseToJava(modelDao).generate(testCase).writeTo(uncheckF(modelDao::exportTestCase));
    }
}
