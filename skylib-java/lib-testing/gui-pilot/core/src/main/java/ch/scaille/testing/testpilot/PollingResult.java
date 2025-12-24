package ch.scaille.testing.testpilot;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @param <C> The type of Component
 * @param <V> The type of returned Value
 */
@NullMarked
public record PollingResult<C, V>(@Nullable V polledValue,
                                  @Nullable Throwable failureCause,
                                  @Nullable PollingMetadata<C> polling) {

    public static <C, V> PollingResult<C, V> value(final V polledValue) {
        return new PollingResult<>(polledValue, null, null);
    }

    public static <C, V> PollingResult<C, V> failure(final Throwable failureCause) {
        return new PollingResult<>(null, failureCause, null);
    }

    public boolean isSuccess() {
        return failureCause == null && polledValue != null;
    }

    public <U> U mapOrElse(Function<V, U> mapper, final Supplier<U> orElse) {
        if (isSuccess()) {
            return mapper.apply(polledValue);
        }
        return orElse.get();
    }

    private PollingMetadata<C> getPolling() {
        return Objects.requireNonNull(polling, "withPolling was never called");
    }

    public Optional<PolledComponent<C>> component() {
        return getPolling().getComponent();
    }

    public GuiPilot getGuiPilot() {
        return getPolling().getGuiPilot();
    }

    public Optional<C> getLoadedElement() {
        return getPolling().getComponent().map(PolledComponent::component);
    }

    public String getComponentDescription() {
        return getPolling().getComponent().map(PolledComponent::description).orElse("No component");
    }

    @Nullable
    public ActionDelay getAndThen() {
        return getPolling().getAndThen();
    }

    public <R> PollingResult<C, R> withValue(R newValue) {
        return new PollingResult<>(newValue, failureCause, null);
    }

    /**
     * Adds the polling parameters to the result, so we can use the texts, ...
     */
    public PollingResult<C, V> withPolling(PollingMetadata<C> polling) {
        return new PollingResult<>(polledValue, failureCause, polling);
    }

    @Override
    public String toString() {
        return "Value: " + polledValue + ", Exception: " + failureCause;
    }
}
