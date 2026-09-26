package ch.scaille.testing.testpilot.factories;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import ch.scaille.testing.testpilot.Polling;
import ch.scaille.testing.testpilot.PolledComponent;
import org.jspecify.annotations.Nullable;

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
		return Polling.of(_ -> PollingResults.success());
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
	 * Succeed if action was applied (that is, no exception or AssertionError was raised)
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
	 * Succeed if action was applied (that is, no exception or AssertionError was raised)
	 */
	public static <C> Predicate<C> satisfiesC(final Consumer<C> assertion) {
		return ctxt -> {
			try {
				assertion.accept(ctxt);
				return true;
			} catch (final AssertionError e) {
				return true;
			}
		};
	}

	/**
	 * Succeed if action was applied (that is, no exception or AssertionError was raised)
	 */
	public static <C> Polling.PollingBuilder<C, Boolean> applies(final Consumer<C> action) {
		return Polling.of(ctxt -> {
			action.accept(ctxt.component());
			return PollingResults.success();
		});
	}

	/**
	 * Returns a value if available (that is, getter is not returning any exception or a null value)
	 * @param getter the getter
	 */
	public static <C, V extends @Nullable Object> Polling.PollingBuilder<C, V> get(Function<C, V> getter) {
		return Polling.of(ctxt -> PollingResults.value(getter.apply(ctxt.component())));
	}

}