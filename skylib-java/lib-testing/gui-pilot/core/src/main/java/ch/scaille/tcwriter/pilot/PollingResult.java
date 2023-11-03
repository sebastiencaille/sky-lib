package ch.scaille.tcwriter.pilot;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import ch.scaille.tcwriter.pilot.AbstractComponentPilot.LoadedComponent;

public class PollingResult<T, V> {

	public interface FailureHandler<C, V> extends BiFunction<PollingResult<C, V>, GuiPilot, V> {
		// simplify type
	}

	public final V polledValue;
	public final Throwable failureReason;
	private String componentDescription;
	private LoadedComponent<T> loadedElement;

	public PollingResult(final V polledValue, final Throwable failureReason) {
		this.polledValue = polledValue;
		this.failureReason = failureReason;
	}

	public Optional<T> getLoadedElement() {
		if (loadedElement == null) {
			return  Optional.empty();
		}
		return Optional.of(loadedElement.element);
	}

	public void setInformation(final String componentDescription, final LoadedComponent<T> loadedElement) {
		this.componentDescription = componentDescription;
		this.loadedElement = loadedElement;
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

	@Override
	public String toString() {
		return "Value: " + polledValue + ", Exception: " + failureReason;
	}

}
