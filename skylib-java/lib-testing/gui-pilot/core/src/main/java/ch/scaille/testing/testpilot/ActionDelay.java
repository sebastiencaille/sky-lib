package ch.scaille.testing.testpilot;

import org.jspecify.annotations.NullMarked;

import java.time.Duration;

/**
 * Represents an arbitrary delay that may precede the action.
 *
 * @author scaille
 *
 */
@NullMarked
public class ActionDelay {

	public static final ActionDelay NO_DELAY = new ActionDelay();

	public void assertFinished() {
		// noop
	}

	public Duration applyOnTimeout(Duration currentDelay) {
		return currentDelay;
	}

}
