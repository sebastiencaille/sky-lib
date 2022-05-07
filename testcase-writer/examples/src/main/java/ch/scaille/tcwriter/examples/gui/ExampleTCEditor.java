package ch.scaille.tcwriter.examples.gui;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import javax.swing.SwingUtilities;

import ch.scaille.gui.mvc.Logging;
import ch.scaille.tcwriter.examples.ExampleHelper;
import ch.scaille.tcwriter.gui.frame.TCWriterController;

public class ExampleTCEditor {

	public static void main(final String[] args) throws IOException {

		final var model = ExampleHelper.generateDictionary();
		final var testCase = ExampleHelper.recordTestCase(model);

		ExampleHelper.getModelDao().writeTestDictionary(testCase.getDictionary());
		ExampleHelper.getModelDao().writeTestCase(ExampleHelper.TC_NAME, testCase);

		final var controller = new TCWriterController(ExampleHelper.getModelDao(), ExampleHelper.testExecutor());

		final var eventDebug = Logging.MVC_EVENTS_DEBUGGER;
		final var eventDebugLevel = Level.FINE;
		eventDebug.setLevel(eventDebugLevel);
		final var handlerConsole = new ConsoleHandler();
		handlerConsole.setFormatter(new SimpleFormatter());
		handlerConsole.setLevel(eventDebugLevel);
		eventDebug.addHandler(handlerConsole);

		SwingUtilities.invokeLater(() -> {
			controller.run();
			controller.loadTestCase(testCase);
		});
	}

}
