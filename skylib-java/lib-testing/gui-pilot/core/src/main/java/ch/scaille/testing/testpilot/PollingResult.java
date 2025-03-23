package ch.scaille.testing.testpilot;

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
	private PollingConfiguration<C> polling;

	public PollingResult(final V polledValue, final Throwable failureReason) {
		this.polledValue = polledValue;
		this.failureReason = failureReason;
	}

	public void setPolling(PollingConfiguration<C> polling) {
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

	public IReporter getGuiPilot() {
		return polling.getContext().getGuiPilot();
	}

	public PollingContext<C> getContext() {
		return polling.getContext();
	}
	
	public PollingConfiguration<C> getPolling() {
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
