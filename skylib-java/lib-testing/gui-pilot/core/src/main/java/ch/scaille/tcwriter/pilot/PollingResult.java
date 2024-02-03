package ch.scaille.tcwriter.pilot;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import ch.scaille.tcwriter.pilot.AbstractComponentPilot.LoadedComponent;

public class PollingResult<T, V> {

	public interface FailureHandler<C, V, R> {
		R apply(PollingResult<C, V> result, GuiPilot guiPilot);
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
			return Optional.empty();
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

	public <U> U mapOrGet(Function<V, U> mapper, final Supplier<U> orElse) {
		if (isSuccess()) {
			return mapper.apply(polledValue);
		}
		return orElse.get();
	}

	@Override
	public String toString() {
		return "Value: " + polledValue + ", Exception: " + failureReason;
	}

}
