package ch.scaille.testing.testpilot;

import java.time.Duration;

/**
 * Represents an arbitrary delay that may precede the action.
 *
 * @author scaille
 *
 */
public class ActionDelay {
	public void assertFinished() {
		// noop
	}

	public Duration applyOnTimeout(Duration currentDelay) {
		return currentDelay;
	}

}
