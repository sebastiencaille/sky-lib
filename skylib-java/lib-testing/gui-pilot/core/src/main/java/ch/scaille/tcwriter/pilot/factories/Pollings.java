package ch.scaille.tcwriter.pilot.factories;

import java.util.function.Consumer;
import java.util.function.Predicate;

import ch.scaille.tcwriter.pilot.Polling;
import ch.scaille.tcwriter.pilot.PollingContext;

public interface Pollings {

	/**
	 * Succeed if the component was found
	 */
	static <C> Polling<C, Boolean> exists() {
		return new Polling<>(c -> PollingResults.success());
	}

	
	/**
	 * Succeed if the Predicate is accepted
	 */
	static <C> Polling<C, Boolean> satisfies(final Predicate<C> predicate) {
		return new Polling<>(c -> {
			if (!predicate.test(c.getComponent())) {
				return PollingResults.failure("Condition not met");
			}
			return PollingResults.success();
		});
	}

	/**
	 * Succeed if the assertion has not failed (no AssertionError raised)
	 */
	static <C> Polling<C, Boolean> asserts(final Consumer<PollingContext<C>> assertion) {
		return new Polling<>(c -> {
			try {
				assertion.accept(c);
				return PollingResults.success();
			} catch (final AssertionError e) {
				return PollingResults.failWithException(e);
			}
		});
	}

	/**
	 * Succeed if action was applied (no exception raised)
	 */
	static <C> Polling<C, Boolean> applies(final Consumer<C> action) {
		return new Polling<>(c -> {
			action.accept(c.getComponent());
			return PollingResults.success();
		});
	}

}