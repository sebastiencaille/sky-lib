package ch.scaille.tcwriter.pilot;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 
 * @param <C> The type of Component
 * @param <V> The type of returned Value
 */
public class PollingResult<C, V> {

	public final V polledValue;
	public final Throwable failureReason;
	private Polling<C, ?> polling;

	public PollingResult(final V polledValue, final Throwable failureReason) {
		this.polledValue = polledValue;
		this.failureReason = failureReason;
	}

	public void setPolling(Polling<C, ?> polling) {
		this.polling = polling;
	}
	
	public Optional<C> getLoadedElement() {
		return Optional.of(polling.getContext().getComponent());
	}

	public String getComponentDescription() {
		return polling.getContext().getDescription();
	}

	public boolean isSuccess() {
		return failureReason == null && polledValue != null;
	}

	public <U> U mapOrGet(Function<V, U> mapper, final Supplier<U> orElse) {
		if (isSuccess()) {
			return mapper.apply(polledValue);
		}
		return orElse.get();
	}

	public GuiPilot getGuiPilot() {
		return polling.getContext().getGuiPilot();
	}

	public PollingContext<C> getContext() {
		return polling.getContext();
	}
	
	public Polling<C, ?> getPolling() {
		return polling;
	}
	
	@Override
	public String toString() {
		return "Value: " + polledValue + ", Exception: " + failureReason;
	}

	public <R> PollingResult<C, R> derivate(R newValue) {
		final var newResult = new PollingResult<C, R>(newValue, failureReason);
		newResult.setPolling(polling);
		return newResult;
	}

	public ActionDelay getActionDelay() {
		return polling.getActionDelay();
	}

}
