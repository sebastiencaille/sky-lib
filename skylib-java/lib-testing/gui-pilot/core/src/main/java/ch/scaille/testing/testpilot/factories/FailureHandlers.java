package ch.scaille.testing.testpilot.factories;

import static ch.scaille.util.helpers.LambdaExt.doNothing;

import ch.scaille.testing.testpilot.PollingResult;

public interface FailureHandlers {
	/*************************** Failure handlers ***************************/

    interface FailureHandler<C, V> {
		void apply(PollingResult<C, V> result);
	}

	/**
	 * Fails using some text
	 */
	static <C, V> FailureHandler<C, V> throwError(final String actionDescription) {
		return r -> {
			throw new AssertionError(
					r.getComponentDescription() + ": action failed [" + actionDescription + "]: " + r.failureCause());
		};
	}

	/**
	 * Throws an AssertionError
	 */
	static <C, V> FailureHandler<C, V> throwError() {
		return r -> {
			final var failureCause = r.failureCause();
			if (failureCause instanceof AssertionError) {
				throw new AssertionError(r.getComponentDescription() + ": " + failureCause.getMessage(),
						failureCause.getCause());
			} else if (failureCause != null) {
				throw new AssertionError(r.getComponentDescription() + ": " + failureCause.getMessage(),
						failureCause);
			} else {
				throw new AssertionError(r.getComponentDescription() + ": failure without cause");
			}
		};
	}

	/**
	 * Only reports error and return a null value
	 */
	static <C, V> FailureHandler<C, V> reportFailure(final String reportLine) {
		return result -> result.getGuiPilot()
				.getActionReport()
				.report(result.getComponentDescription() + ": " + reportLine);
	}

	/**
	 * Only reports error and return FALSE
	 */
	static <C, V> FailureHandler<C, V> reportNotSatisfied(final String reportLine) {
		return result -> result.getGuiPilot()
				.getActionReport()
				.report(result.getComponentDescription() + ": " + reportLine);
	}

	/**
	 * Do nothing on error
	 */
	static <C, V> FailureHandler<C, V> ignoreFailure() {
		return result -> doNothing();
	}
}
