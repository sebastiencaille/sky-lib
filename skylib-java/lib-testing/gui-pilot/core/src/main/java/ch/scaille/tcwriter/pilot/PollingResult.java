package ch.scaille.tcwriter.pilot;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import ch.scaille.tcwriter.pilot.AbstractComponentPilot.LoadedComponent;

public class PollingResult<C, V> {

	public interface FailureHandler<C, V, R> {
		R apply(PollingResult<C, V> result, GuiPilot guiPilot);
	}

	public final V polledValue;
	public final Throwable failureReason;
	private Polling<C, V> polling;
	private LoadedComponent<C> loadedElement;

	public PollingResult(final V polledValue, final Throwable failureReason) {
		this.polledValue = polledValue;
		this.failureReason = failureReason;
	}

	public void setPolling(Polling<C, V> polling) {
		this.polling = polling;
	}
	
	public Polling<C, V> getPolling() {
		return polling;
	}
	
	public Optional<C> getLoadedElement() {
		if (loadedElement == null) {
			return Optional.empty();
		}
		return Optional.of(loadedElement.element);
	}

	public String getComponentDescription() {
		return getPolling().getContext().getDescription();
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
