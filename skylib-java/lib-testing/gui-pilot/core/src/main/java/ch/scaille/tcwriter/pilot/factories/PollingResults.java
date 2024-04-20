package ch.scaille.tcwriter.pilot.factories;

import java.util.function.Function;

import ch.scaille.tcwriter.pilot.PollingResult;

public interface PollingResults {
	
	public interface Transformer<C, V, R> extends Function<PollingResult<C, V>, PollingResult<C, R>> {
		// noop
	}
	
	/**
	 * Creates a successful polling with a value
	 *
	 * @param <C>
	 * @param <V>
	 * @param value the value
	 * @return
	 */
	static <C, V> PollingResult<C, V> value(final V value) {
		return new PollingResult<>(value, null);
	}

	/**
	 * Creates a successful polling without value
	 *
	 * @param <C>
	 * @return
	 */
	static <C> PollingResult<C, Boolean> success() {
		return new PollingResult<>(Boolean.TRUE, null);
	}

	/**
	 * Creates a failed polling without value
	 *
	 * @param <C>
	 * @return
	 */
	static <C, V> PollingResult<C, V> failed() {
		return failure("Failed");
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

	static <C> Transformer<C, ?, Boolean> returnSuccess() {
		return r -> r.derivate(r.isSuccess());
	}
	
	static <C, R> Transformer<C, R, R> identity() {
		return r -> r;
	}
}
