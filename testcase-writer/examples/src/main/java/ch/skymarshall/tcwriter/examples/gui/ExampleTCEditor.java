package ch.skymarshall.tcwriter.examples.gui;

import java.io.IOException;

import javax.swing.SwingUtilities;

import ch.skymarshall.tcwriter.examples.ExampleHelper;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.gui.frame.TCWriterController;

public class ExampleTCEditor {

	public static void main(final String[] args) throws IOException {

		final TestModel model = ExampleHelper.generateModel();
		final TestCase testCase = ExampleHelper.recordTestCase(model);

		ExampleHelper.saveModel(testCase.getModel());
		ExampleHelper.saveTC(ExampleHelper.TC_NAME, testCase);

		final TCWriterController controller = new TCWriterController(ExampleHelper.getConfig(),
				ExampleHelper.getPersister(), ExampleHelper.testExecutor());

		SwingUtilities.invokeLater(() -> {
			controller.run();
			controller.loadTestCase(testCase);
		});
	}

}
