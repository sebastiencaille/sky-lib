package ch.scaille.tcwriter.pilot.factories;

import java.util.function.Function;

import ch.scaille.tcwriter.pilot.PollingResult;

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
	 * Creates a successful polling with a value
	 */
	static <C, V> PollingResult<C, V> value(final V value) {
		return new PollingResult<>(value, null);
	}

	/**
	 * Creates a successful polling without value
	 */
	static <C> PollingResult<C, Boolean> success() {
		return new PollingResult<>(Boolean.TRUE, null);
	}

	/**
	 * Creates a failed polling without value
	 */
	static <C, V> PollingResult<C, V> failed() {
		return failure("Failed");
	}

	/**
	 * Creates a failed polling with a reason for the failure 
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
		return r -> r.derivate(r.isSuccess());
	}
	
	static <C, R> Transformer<C, R, R> identity() {
		return r -> r;
	}
}
