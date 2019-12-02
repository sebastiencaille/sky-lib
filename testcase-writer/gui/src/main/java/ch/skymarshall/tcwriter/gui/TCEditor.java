package ch.skymarshall.tcwriter.gui;

import static ch.skymarshall.tcwriter.generators.JsonHelper.testModelFromJson;

import java.io.File;
import java.io.IOException;

import ch.skymarshall.tcwriter.generators.JsonHelper;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.util.helpers.ClassLoaderHelper;
import executors.ITestExecutor;
import executors.JunitTestExecutor;

public class TCEditor {

	public static void main(final String[] args) throws IOException {
		final String modelFile = args[0];
		final String javaTemplate = args[1];
		final String javaTargetPath = args[2];
		final TestModel testModel = testModelFromJson(JsonHelper.readFile(new File(modelFile).toPath()));
		final ITestExecutor testExecutor = new JunitTestExecutor(new File(javaTemplate).toPath(),
				new File(javaTargetPath).toPath(), ClassLoaderHelper.appClassPath());
		new TCWriterGui(testModel, testExecutor).setVisible(true);
	}

}
