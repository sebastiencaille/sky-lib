package ch.skymarshall.tcwriter.pilot;

import java.util.function.Function;
import java.util.function.Supplier;

import ch.skymarshall.tcwriter.pilot.AbstractGuiComponent.LoadedElement;

public class PollingResult<T, V> {
	public final V polledValue;
	public final Throwable failureReason;
	private GuiPilot pilot;
	private String componentDescription;
	private LoadedElement<T> loadedElement;

	public PollingResult(final V polledValue, final Throwable failureReason) {
		this.polledValue = polledValue;
		this.failureReason = failureReason;
	}

	public T getFoundElement() {
		if (loadedElement != null) {
			return loadedElement.element;
		}
		return null;
	}

	public void setInformation(final GuiPilot pilot, final String componentDescription,
			final LoadedElement<T> loadedElement) {
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

	public boolean isSuccess() {
		return failureReason == null;
	}

	public V orElse(final V orElse) {
		if (isSuccess()) {
			return polledValue;
		}
		return orElse;
	}

	public V orElseGet(final Supplier<V> orElse) {
		if (isSuccess()) {
			return polledValue;
		}
		return orElse.get();
	}

	/* *********************************************************** */

	/* *********************************************************** */

	/**
	 * Make polling return a value
	 *
	 * @param <C>
	 * @param <V>
	 * @param value
	 * @return
	 */
	public static <C, V> PollingResult<C, V> value(final V value) {
		return new PollingResult<>(value, null);
	}

	/**
	 * Make polling return value "true"
	 *
	 * @param <C>
	 * @return
	 */
	public static <C> PollingResult<C, Boolean> success() {
		return new PollingResult<>(Boolean.TRUE, null);
	}

	/**
	 * Make polling return value "false"
	 *
	 * @param <C>
	 * @return
	 */
	public static <C> PollingResult<C, Boolean> failed() {
		return new PollingResult<>(Boolean.FALSE, null);
	}

	/**
	 * Make polling return a failure
	 *
	 * @param <C>
	 * @return
	 */
	public static <C, V> PollingResult<C, V> failure(final String reason) {
		return new PollingResult<>(null, new RuntimeException(reason));
	}

	/**
	 * Make polling return a failure
	 *
	 * @param <C>
	 * @return
	 */
	public static <C, V> PollingResult<C, V> onException(final Throwable cause) {
		return new PollingResult<>(null, cause);
	}

	/* *********************************************************** */

	public interface PollingResultFunction<C, V> extends Function<PollingResult<C, V>, V> {
		// simplify type
	}

	/**
	 * Fails using some text
	 *
	 * @param actionDescr
	 * @return
	 */
	public static <C, V> PollingResultFunction<C, V> assertFail(final String actionDescr) {
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
	public static <C, V> PollingResultFunction<C, V> throwError() {
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
	public static <C> PollingResultFunction<C, Boolean> reportFailed(final String report) {
		return r -> {
			r.getPilot().getActionReport().report(report);
			return Boolean.FALSE;
		};
	}

	/* *********************************************************** */

}
