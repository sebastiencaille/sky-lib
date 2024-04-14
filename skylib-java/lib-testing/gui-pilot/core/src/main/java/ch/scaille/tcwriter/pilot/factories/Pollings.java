package ch.scaille.tcwriter.pilot.factories;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import ch.scaille.tcwriter.pilot.Polling;
import ch.scaille.tcwriter.pilot.PollingContext;

public interface Pollings {

	/**
	 * Succeed if the component was found
	 */
	static <C> Polling<C, Boolean> exists() {
		return new Polling<>(ctxt -> PollingResults.success());
	}

	
	/**
	 * Succeed if the Predicate is accepted
	 */
	static <C> Polling<C, Boolean> satisfies(final Predicate<C> predicate) {
		return new Polling<>(ctxt -> {
			if (!predicate.test(ctxt.getComponent())) {
				return PollingResults.failure("Condition not met");
			}
			return PollingResults.success();
		});
	}

	/**
	 * Succeed if the assertion has not failed (no AssertionError raised)
	 */
	static <C> Polling<C, Boolean> asserts(final Consumer<PollingContext<C>> assertion) {
		return new Polling<>(ctxt -> {
			try {
				assertion.accept(ctxt);
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
		return new Polling<>(ctxt -> {
			action.accept(ctxt.getComponent());
			return PollingResults.success();
		});
	}


	static <C, V> Polling<C, V> get(Function<C, V> getter) {
		return new Polling<>(ctxt -> PollingResults.value(getter.apply(ctxt.getComponent())));
	}

}