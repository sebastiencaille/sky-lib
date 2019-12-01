package ch.skymarshall.tcwriter.examples.gui;

import static ch.skymarshall.tcwriter.examples.ExampleHelper.testExecutor;
import static ch.skymarshall.tcwriter.generators.JsonHelper.readFile;
import static ch.skymarshall.tcwriter.generators.JsonHelper.testModelFromJson;

import java.io.IOException;

import javax.swing.SwingUtilities;

import ch.skymarshall.tcwriter.examples.ExampleHelper;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.gui.TCWriterGui;

public class ExampleTCEditor {

	public static void main(final String[] args) throws IOException {

		ExampleHelper.RESOURCE_FOLDER.mkdirs();

		final TestModel model = ExampleHelper.generateModel();
		final TestCase testCase = ExampleHelper.recordTestCase(model);

		ExampleHelper.saveModel(testCase.getModel());
		ExampleHelper.saveTC(testCase);

		final TCWriterGui exampleTCWriter = new TCWriterGui(testModelFromJson(readFile(ExampleHelper.MODEL_PATH)),
				testExecutor());
		SwingUtilities.invokeLater(() -> {
			exampleTCWriter.run();
			exampleTCWriter.loadTestCase(testCase);
		});
	}

}
