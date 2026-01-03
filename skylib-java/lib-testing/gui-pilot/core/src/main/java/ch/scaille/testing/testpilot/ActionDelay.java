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
public interface ActionDelay {

	 ActionDelay NO_DELAY = currentDelay -> currentDelay;

	 default void assertFinished() {
		// noop
	}

	Duration applyOnTimeout(Duration currentDelay);

}
