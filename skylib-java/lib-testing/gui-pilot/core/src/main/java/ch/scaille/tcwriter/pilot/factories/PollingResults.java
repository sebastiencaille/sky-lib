package ch.scaille.tcwriter.pilot.factories;

import ch.scaille.tcwriter.pilot.PollingResult;

public interface PollingResults {
	/**
	 * Make polling return a value
	 *
	 * @param <C>
	 * @param <V>
	 * @param value
	 * @return
	 */
	static <C, V> PollingResult<C, V> value(final V value) {
		return new PollingResult<>(value, null);
	}

	/**
	 * Make polling return value "true"
	 *
	 * @param <C>
	 * @return
	 */
	static <C> PollingResult<C, Boolean> success() {
		return new PollingResult<>(Boolean.TRUE, null);
	}

	/**
	 * Make polling return value "false"
	 *
	 * @param <C>
	 * @return
	 */
	static <C> PollingResult<C, Boolean> failed() {
		return new PollingResult<>(Boolean.FALSE, null);
	}

	/**
	 * Make polling return a failure
	 *
	 * @param <C>
	 * @return
	 */
	static <C, V> PollingResult<C, V> failure(final String reason) {
		return new PollingResult<>(null, new AssertionError(reason));
	}

	/**
	 * Make polling return a failure
	 *
	 * @param <C>
	 * @return
	 */
	static <C, V> PollingResult<C, V> failWithException(final Throwable cause) {
		return new PollingResult<>(null, cause);
	}

}
