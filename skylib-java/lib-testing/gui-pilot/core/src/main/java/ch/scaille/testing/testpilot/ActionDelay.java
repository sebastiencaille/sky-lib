package ch.scaille.testing.testpilot;

import java.time.Duration;

/**
 * Represents an arbitrary delay that may precede the action.
 *
 */

public interface ActionDelay {

	 ActionDelay NO_DELAY = currentDelay -> currentDelay;

	 default void assertFinished() {
		// noop
	}

	Duration applyOnTimeout(Duration currentDelay);

}
