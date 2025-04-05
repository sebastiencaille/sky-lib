package ch.scaille.testing.testpilot;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 
 * @param <C> The type of Component
 * @param <V> The type of returned Value
 */
public record PollingResult<C, V>(V polledValue, 
		Throwable failureReason, 
		PollingMetadata<C> polling) {

	public PollingResult(final V polledValue, final Throwable failureReason) {
		this(polledValue, failureReason, null);
	}

	public boolean isSuccess() {
		return failureReason == null && polledValue != null;
	}

	public <U> U mapOrElse(Function<V, U> mapper, final Supplier<U> orElse) {
		if (isSuccess()) {
			return mapper.apply(polledValue);
		}
		return orElse.get();
	}
	
	public PollingContext<C> context() {
		return polling.getContext();
	}
	
	public GuiPilot getGuiPilot() {
		return polling.getContext().getGuiPilot();
	}

	public Optional<C> getLoadedElement() {
		return Optional.of(polling.getContext().getComponent());
	}

	public String getComponentDescription() {
		return polling.getContext().getDescription();
	}

	public ActionDelay getActionDelay() {
		return polling.getActionDelay();
	}

	public <R> PollingResult<C, R> withValue(R newValue) {
		return new PollingResult<>(newValue, failureReason, null);
	}

	public PollingResult<C, V> withPolling(PollingMetadata<C> polling) {
		return new PollingResult<>(polledValue, failureReason, polling);
	}

	@Override
	public String toString() {
		return "Value: " + polledValue + ", Exception: " + failureReason;
	}
}
