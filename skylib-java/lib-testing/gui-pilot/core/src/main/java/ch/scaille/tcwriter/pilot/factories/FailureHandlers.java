package ch.scaille.tcwriter.pilot.factories;

import ch.scaille.tcwriter.pilot.PollingResult.FailureHandler;

public interface FailureHandlers {
	/*************************** Failure handlers ***************************/

	/**
	 * Fails using some text
	 *
	 * @param actionDescr
	 * @return
	 */
	static <C, V> FailureHandler<C, V, V> throwError(final String actionDescr) {
		return (r, g) -> {
			throw new AssertionError(
					r.getComponentDescription() + ": action failed [" + actionDescr + "]: " + r.failureReason);
		};
	}

	/**
	 * Throws an AssertionError
	 *
	 * @return
	 */
	static <C, V> FailureHandler<C, V, V> throwError() {
		return (r, g) -> {
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
	 *
	 * @param actionDescr
	 * @return
	 */
	static <C, V> FailureHandler<C, V, V> reportFailure(final String report) {
		return (r, g) -> {
			g.getActionReport().report(r.getComponentDescription() + ": " + report);
			return r.polledValue;
		};
	}

	/**
	 * Only reports error and return FALSE
	 *
	 * @param actionDescr
	 * @return
	 */
	static <C, V> FailureHandler<C, V, Boolean> reportNotSatisfied(final String report) {
		return (result, pilot) -> {
			pilot.getActionReport().report(result.getComponentDescription() + ": " + report);
			return Boolean.FALSE;
		};
	}
	
	/**
	 * Do nothing on error and return a null value
	 *
	 * @param actionDescr
	 * @return
	 */
	static <C, V> FailureHandler<C, V, V> returnNull() {
		return (r, g) -> r.polledValue;
	}


	/**
	 * Do nothing on error and return FALSE
	 *
	 * @param actionDescr
	 * @return
	 */
	static <C, P> FailureHandler<C, P, Boolean> ignoreFailure() {
		return (r, g) -> Boolean.FALSE;
	}
}
