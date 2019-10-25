package ch.skymarshall.tcwriter.gui;

import static ch.skymarshall.tcwriter.generators.visitors.JsonHelper.testModelFromJson;

import java.io.File;
import java.io.IOException;

import ch.skymarshall.tcwriter.generators.TestCaseToJava;
import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.visitors.JsonHelper;

public class TCEditor {

	public static void main(final String[] args) throws IOException {
		final String modelFile = args[0];
		final String javaTemplate = args[1];
		final String javaTargetPath = args[2];
		final TestModel testModel = testModelFromJson(JsonHelper.readFile(new File(modelFile).toPath()));
		new TCWriterGui(testModel) {

			@Override
			public File generateCode(final TestCase tc) throws TestCaseException, IOException {
				return new TestCaseToJava(new File(javaTemplate).toPath()).generateAndWrite(tc,
						new File(javaTargetPath).toPath());
			}
		}.setVisible(true);
	}

}
