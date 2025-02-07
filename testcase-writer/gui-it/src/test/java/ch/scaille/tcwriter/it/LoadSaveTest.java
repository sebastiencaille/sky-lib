package ch.scaille.tcwriter.it;

import static ch.scaille.tcwriter.it.api.MainFrameAction.loadTC;
import static ch.scaille.tcwriter.it.api.MainFrameAction.newTC;
import static ch.scaille.tcwriter.it.api.MainFrameAction.saveTC;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import ch.scaille.tcwriter.it.api.TestContent;
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
		final var test = TestContent.basicTestContent();
		
		testSession.injectTest(test);
		tcWriter.manageTest(saveTC("test"));
		testSession.assertTest(test);
		
		tcWriter.manageTest(newTC());
		tcWriter.manageTest(loadTC("test"));
		testSession.assertTest(test);
	}

}
