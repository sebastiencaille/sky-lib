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
			if (r.failureCause() instanceof AssertionError) {
				throw new AssertionError(r.getComponentDescription() + ": " + r.failureCause().getMessage(),
						r.failureCause().getCause());
			}
			throw new AssertionError(r.getComponentDescription() + ": " + r.failureCause().getMessage(),
					r.failureCause());
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
