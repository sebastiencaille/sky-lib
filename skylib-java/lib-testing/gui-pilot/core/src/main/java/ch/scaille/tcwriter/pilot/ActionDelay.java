package ch.scaille.tcwriter.pilot;

import java.time.Duration;

/**
 * Represents an arbitrary delay that may precede the action.
 *
 * @author scaille
 *
 */
public class ActionDelay {
	public void waitFinished() {
		// noop
	}

	public Duration applyOnTimeout(Duration currentDelay) {
		return currentDelay;
	}

}
