package ch.scaille.testing.testpilot.factories;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import ch.scaille.testing.testpilot.Polling;
import ch.scaille.testing.testpilot.PolledComponent;

/**
 * Polling factories
 */
public abstract class Pollings {

	protected Pollings() {
		// noop
	}
	
	/**
	 * Succeed if the component was found
	 */
	public static <C> Polling.PollingBuilder<C, Boolean> exists() {
		return Polling.of(ctxt -> PollingResults.success());
	}

	
	/**
	 * Succeed if the Predicate is accepted
	 */
	public static <C> Polling.PollingBuilder<C, Boolean> satisfies(final Predicate<C> predicate) {
		return Polling.of(ctxt -> {
			if (!predicate.test(ctxt.component())) {
				return PollingResults.failure("Condition not met");
			}
			return PollingResults.success();
		});
	}

	/**
	 * Succeed if the assertion has not failed (no AssertionError raised)
	 */
	public static <C> Polling.PollingBuilder<C, Boolean> appliesCtxt(final Consumer<PolledComponent<C>> assertion) {
		return Polling.of(ctxt -> {
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
	public static <C> Polling.PollingBuilder<C, Boolean> applies(final Consumer<C> action) {
		return Polling.of(ctxt -> {
			action.accept(ctxt.component());
			return PollingResults.success();
		});
	}


	public static <C, V> Polling.PollingBuilder<C, V> get(Function<C, V> getter) {
		return Polling.of(ctxt -> PollingResults.value(getter.apply(ctxt.component())));
	}

}