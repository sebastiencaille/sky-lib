package ch.scaille.tcwriter.it;

import static ch.scaille.tcwriter.it.api.MainFrameAction.loadTC;
import static ch.scaille.tcwriter.it.api.MainFrameAction.newTC;
import static ch.scaille.tcwriter.it.api.MainFrameAction.saveTC;

import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import ch.scaille.tcwriter.it.api.TestContent;
import ch.scaille.testing.testpilot.jupiter.DisabledIfHeadless;
import ch.scaille.util.helpers.Logs;

@ExtendWith(DisabledIfHeadless.class)
class LoadSaveTest extends AbstractGuiTest {

	@BeforeAll
	static void initLog() {
		final var rootLog = Logs.of("ch");
		rootLog.setLevel(Level.ALL);
		
		final var logConsole = new ConsoleHandler();
		logConsole.setLevel(Level.ALL);
		rootLog.addHandler(logConsole);
	}

	@Test
	void testSaveNewLoad() {
		final var test = TestContent.basicTestContent();
		Objects.requireNonNull(testSession);
		Objects.requireNonNull(tcWriter);

		testSession.injectTest(test);
		testSession.assertTest(test);
		tcWriter.manageTest(saveTC("test"));
		
		tcWriter.manageTest(newTC());
		tcWriter.manageTest(loadTC("test"));
		testSession.assertTest(test);
	}

}
