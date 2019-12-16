package ch.skymarshall.tcwriter.examples.gui;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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

		final Logger eventDebug = Logger.getLogger("MvcEventsDebug");
		Level eventDebugLevel = Level.FINE;
		eventDebug.setLevel(eventDebugLevel);
		final ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new SimpleFormatter());
		handler.setLevel(eventDebugLevel);
		eventDebug.addHandler(handler);

		SwingUtilities.invokeLater(() -> {
			controller.run();
			controller.loadTestCase(testCase);
		});
	}

}
