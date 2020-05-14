package ch.skymarshall.tcwriter.pilot;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import ch.skymarshall.tcwriter.pilot.AbstractGuiComponent.LoadedElement;

public class Polling<TT, U> {
	public final U value;
	public final Throwable failureReason;
	private GuiPilot pilot;
	private String componentDescription;
	private LoadedElement<TT> loadedElement;

	public Polling(final U value, final Throwable failureReason) {
		this.value = value;
		this.failureReason = failureReason;
	}

	public TT getFoundElement() {
		if (loadedElement != null) {
			return loadedElement.element;
		}
		return null;
	}

	public void setInformation(final GuiPilot pilot, final String componentDescription,
			final LoadedElement<TT> loadedElement) {
		this.pilot = pilot;
		this.componentDescription = componentDescription;
		this.loadedElement = loadedElement;
	}

	public GuiPilot getPilot() {
		return pilot;
	}

	public String getComponentDescription() {
		return componentDescription;
	}

	public boolean success() {
		return failureReason == null;
	}

	public U orElse(final U orElse) {
		if (success()) {
			return value;
		}
		return orElse;
	}

	public U orElseGet(final Supplier<U> orElse) {
		if (success()) {
			return value;
		}
		return orElse.get();
	}

	/* *********************************************************** */

	public interface PollingFunction<TT, U> extends Function<TT, Polling<TT, U>> {
		// simplify type
	}

	public static <TT> PollingFunction<TT, Boolean> action(final Consumer<TT> consumer) {
		return t -> {
			consumer.accept(t);
			return isTrue();
		};
	}

	/**
	 * Make polling return true if condition matches
	 *
	 * @param <TT>
	 * @param <U>
	 * @param reason
	 * @return
	 */
	public static <TT> PollingFunction<TT, Boolean> matches(final Predicate<TT> predicate) {
		return t -> {
			if (!predicate.test(t)) {
				return new Polling<>(null, new RuntimeException("Condition not met"));
			}
			return isTrue();
		};
	}

	/**
	 * Make polling return true if assert is successful
	 *
	 * @param <TT>
	 * @param <U>
	 * @param reason
	 * @return
	 */
	public static <TT> PollingFunction<TT, Boolean> assertion(final Consumer<TT> assertion) {
		return t -> {
			try {
				assertion.accept(t);
				return isTrue();
			} catch (final AssertionError e) {
				return new Polling<>(null, e);
			}
		};
	}

	/* *********************************************************** */

	/**
	 * Make polling return a value
	 *
	 * @param <TT>
	 * @param <U>
	 * @param value
	 * @return
	 */
	public static <TT, U> Polling<TT, U> value(final U value) {
		return new Polling<>(value, null);
	}

	/**
	 * Make polling return value "true"
	 *
	 * @param <TT>
	 * @return
	 */
	public static <TT> Polling<TT, Boolean> isTrue() {
		return new Polling<>(Boolean.TRUE, null);
	}

	/**
	 * Make polling return value "false"
	 *
	 * @param <TT>
	 * @return
	 */
	public static <TT> Polling<TT, Boolean> isFalse() {
		return new Polling<>(Boolean.FALSE, null);
	}

	/**
	 * Make polling return a failure
	 *
	 * @param <TT>
	 * @return
	 */
	public static <TT, U> Polling<TT, U> failure(final String reason) {
		return new Polling<>(null, new RuntimeException(reason));
	}

	/**
	 * Make polling return a failure
	 *
	 * @param <TT>
	 * @return
	 */
	public static <TT, U> Polling<TT, U> onException(final Throwable cause) {
		return new Polling<>(null, cause);
	}

	/* *********************************************************** */

	public interface PollingResultFunction<TT, U> extends Function<Polling<TT, U>, U> {
		// simplify type
	}

	/**
	 * Fails using some text
	 *
	 * @param actionDescr
	 * @return
	 */
	public static <TT, U> PollingResultFunction<TT, U> assertFail(final String actionDescr) {
		return r -> {
			throw new AssertionError(
					r.componentDescription + ": action failed [" + actionDescr + "]: " + r.failureReason);
		};
	}

	/**
	 * Throws an AssertionError
	 *
	 * @return
	 */
	public static <TT, U> PollingResultFunction<TT, U> throwError() {
		return r -> {
			if (r.failureReason instanceof AssertionError) {
				throw new AssertionError(r.componentDescription + ": " + r.failureReason.getMessage(),
						r.failureReason.getCause());
			}
			throw new AssertionError(r.componentDescription + ": " + r.failureReason.getMessage(), r.failureReason);
		};
	}

	/**
	 * Fails using existing error
	 *
	 * @param actionDescr
	 * @return
	 */
	public static <TT> PollingResultFunction<TT, Boolean> report(final String report) {
		return r -> {
			r.getPilot().getActionReport().report(report);
			return Boolean.FALSE;
		};
	}

	/* *********************************************************** */

}
