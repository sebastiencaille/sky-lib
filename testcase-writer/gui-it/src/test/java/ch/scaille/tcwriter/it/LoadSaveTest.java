package ch.scaille.tcwriter.it;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import ch.scaille.tcwriter.it.api.MainFrameAction;
import ch.scaille.tcwriter.jupiter.DisabledIfHeadless;
import ch.scaille.util.helpers.Logs;

@ExtendWith(DisabledIfHeadless.class)
class LoadSaveTest extends AbstractGuiTest {

	@BeforeAll
	static void initLogger() {
		final var rootLogger = Logs.of("ch");
		rootLogger.setLevel(Level.ALL);
		final var logConsole = new ConsoleHandler();
		logConsole.setLevel(Level.ALL);
		rootLogger.addHandler(logConsole);
	}

	@Test
	void testSaveNewLoad() {
		testSession.injectBasicTest();
		tcWriter.mainFrameAction(MainFrameAction.saveTC("test"));
		testSession.assertBasicTest();
		tcWriter.mainFrameAction(MainFrameAction.newTC());
		tcWriter.mainFrameAction(MainFrameAction.loadTC("test"));
		testSession.assertBasicTest();
	}

}
