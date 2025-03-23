package ch.scaille.testing.testpilot.factories;

import java.util.function.Function;

import ch.scaille.testing.testpilot.PollingResult;

public interface PollingResults {
	
	/**
	 * @param <C> Component type
	 * @param <V> Value type
	 * @param <R> Result type
	 */
	interface Transformer<C, V, R> extends Function<PollingResult<C, V>, PollingResult<C, R>> {
		// noop
	}
	
	/**
	 * Creates a polling success with a value
	 */
	static <C, V> PollingResult<C, V> value(final V value) {
		return new PollingResult<>(value, null);
	}

	/**
	 * Creates a polling success without any value
	 */
	static <C> PollingResult<C, Boolean> success() {
		return new PollingResult<>(Boolean.TRUE, null);
	}

	/**
	 * Creates a polling failure without any value
	 */
	static <C, V> PollingResult<C, V> failed() {
		return failure("Failed");
	}

	/**
	 * Creates a polling failure with a reason for the failure 
	 */
	static <C, V> PollingResult<C, V> failure(final String reason) {
		return new PollingResult<>(null, new AssertionError(reason));
	}

	/**
	 * Creates a failed polling with a cause for the failure 
	 */
	static <C, V> PollingResult<C, V> failWithException(final Throwable cause) {
		return new PollingResult<>(null, cause);
	}

	static <C, V> Transformer<C, V, Boolean> returnSuccess() {
		return r -> r.withValue(r.isSuccess());
	}
	
	static <C, R> Transformer<C, R, R> identity() {
		return r -> r;
	}
}
