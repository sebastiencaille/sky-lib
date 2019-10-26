package ch.skymarshall.tcwriter.examples.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.SwingUtilities;

import ch.skymarshall.tcwriter.examples.ExampleHelper;
import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.visitors.JsonHelper;
import ch.skymarshall.tcwriter.gui.TCWriterGui;

public class ExampleTCEditor extends TCWriterGui {

	public ExampleTCEditor(final Path modelPath) throws IOException {
		super(JsonHelper.testModelFromJson(Files.readAllLines(modelPath).get(0)));
	}

	@Override
	public File generateCode(final TestCase tc) throws TestCaseException, IOException {
		return ExampleHelper.generateCode(tc);
	}

	public static void main(final String[] args) throws IOException {

		ExampleHelper.RESOURCE_FOLDER.mkdirs();

		final TestModel model = ExampleHelper.generateModel();
		final TestCase testCase = ExampleHelper.recordTestCase(model);

		ExampleHelper.saveModel(testCase.getModel());
		ExampleHelper.saveTC(testCase);

		final ExampleTCEditor exampleTCWriter = new ExampleTCEditor(ExampleHelper.MODEL_PATH);
		SwingUtilities.invokeLater(() -> {
			exampleTCWriter.run();
			try {
				exampleTCWriter.loadTestCase(ExampleHelper.TC_PATH);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		});
	}

}
