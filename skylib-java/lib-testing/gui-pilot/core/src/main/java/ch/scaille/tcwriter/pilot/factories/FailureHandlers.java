package ch.scaille.tcwriter.pilot.factories;

import ch.scaille.tcwriter.pilot.PollingResult;
import ch.scaille.util.helpers.LambdaExt;

public interface FailureHandlers {
	/*************************** Failure handlers ***************************/

    interface FailureHandler<C, V> {
		void apply(PollingResult<C, V> result);
	}

	/**
	 * Fails using some text
	 */
	static <C, V> FailureHandler<C, V> throwError(final String actionDescr) {
		return r -> {
			throw new AssertionError(
					r.getComponentDescription() + ": action failed [" + actionDescr + "]: " + r.failureReason);
		};
	}

	/**
	 * Throws an AssertionError
	 */
	static <C, V> FailureHandler<C, V> throwError() {
		return r -> {
			if (r.failureReason instanceof AssertionError) {
				throw new AssertionError(r.getComponentDescription() + ": " + r.failureReason.getMessage(),
						r.failureReason.getCause());
			}
			throw new AssertionError(r.getComponentDescription() + ": " + r.failureReason.getMessage(),
					r.failureReason);
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
		return result -> LambdaExt.doNothing();
	}
}
