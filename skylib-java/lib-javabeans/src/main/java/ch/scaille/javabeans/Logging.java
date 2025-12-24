package ch.scaille.javabeans;

import java.util.logging.Logger;

import ch.scaille.util.helpers.Logs;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class Logging {

	public static final Logger MVC_EVENTS_DEBUGGER = Logs.of("MvcEventsDebug");

	private Logging() {
	}

}
