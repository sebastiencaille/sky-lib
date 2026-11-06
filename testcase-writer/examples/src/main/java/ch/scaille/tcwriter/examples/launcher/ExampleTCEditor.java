package ch.scaille.tcwriter.examples.launcher;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import javax.swing.SwingUtilities;

import org.apiguardian.api.API;
import org.junit.jupiter.api.Test;

import ch.scaille.javabeans.Logging;
import ch.scaille.tcwriter.examples.ExampleFactory;
import ch.scaille.tcwriter.gui.frame.TCWriterController;
import ch.scaille.util.helpers.JavaExt;

public class ExampleTCEditor {

	static void main() throws IOException {
		final var exampleFactory = new ExampleFactory(
				JavaExt.locationOf(ExampleTCEditor.class).toString(), 
				JavaExt.locationOf(Test.class).toString(),
				JavaExt.locationOf(API.class).toString());
		final var tcDictionary = exampleFactory.generateDictionary();
		final var testCase = exampleFactory.recordTestCase(tcDictionary);

		exampleFactory.getModelDao().writeTestDictionary(testCase.getDictionary());
		exampleFactory.getModelDao().writeTestCase(ExampleFactory.TC_NAME, testCase);

		final var controller = new TCWriterController(exampleFactory.getConfigDao(), exampleFactory.getModelDao(),
				tcDictionary, exampleFactory.testExecutor());

		final var eventDebug = Logging.MVC_EVENTS_DEBUGGER;
		final var eventDebugLevel = Level.FINE;
		eventDebug.setLevel(eventDebugLevel);
		final var handlerConsole = new ConsoleHandler();
		handlerConsole.setFormatter(new SimpleFormatter());
		handlerConsole.setLevel(eventDebugLevel);
		eventDebug.addHandler(handlerConsole);

		SwingUtilities.invokeLater(() -> {
			controller.start();
			controller.loadTestCase(testCase);
		});
	}

}
