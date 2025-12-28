package ch.scaille.testing.testpilot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

import ch.scaille.testing.testpilot.PilotReport.ReportFunction;
import ch.scaille.testing.testpilot.factories.PollingResults;
import ch.scaille.testing.testpilot.factories.FailureHandlers.FailureHandler;
import ch.scaille.util.helpers.DelayFunction;
import ch.scaille.util.helpers.Logs;
import ch.scaille.util.helpers.Poller;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Class that allows to poll graphical components
 *
 * @param <C> Component type
 */
@NullMarked
public abstract class AbstractComponentPilot<C> {

    private final Logger logger = Logs.of(this);

    protected static class LoadedComponent<T> {
        public final T element;

        private boolean preconditionValidated;

        public LoadedComponent(final T element) {
            this.element = element;
        }

        @Override
        public String toString() {
            return element + ", precondition validated=" + preconditionValidated;
        }

    }

    /**
     * Loads a component from the gui
     */
    protected abstract Optional<C> loadGuiComponent();

    /**
     * Checks if a component is in a state that allows checking its state
     */
    public abstract boolean canCheck(final PolledComponent<C> ctxt);

    @Getter
    private final GuiPilot pilot;

    private final List<Consumer<C>> postExecutions = new ArrayList<>();
    
    @Nullable 
    private LoadedComponent<C> cachedComponent = null;

    protected boolean fired = false;

    protected AbstractComponentPilot(final GuiPilot pilot) {
        this.pilot = pilot;
    }

    protected Optional<String> getDescription() {
        return getCachedElement().map(Object::toString);
    }

    public Optional<C> getCachedElement() {
        return Optional.ofNullable(cachedComponent)
                .map(component -> component.element);
    }

    private LoadedComponent<C> getCachedComponent() {
        return Objects.requireNonNull(cachedComponent, "Component not loaded yet");
    }

    protected void invalidateCache() {
        if (fired) {
            throw new IllegalStateException("Action was already fired");
        }
        cachedComponent = null;
    }

    protected Duration getDefaultPollingTimeout() {
        return pilot.getDefaultPollingTimeout();
    }

    protected Duration getDefaultPollingFirstDelay() {
        return pilot.getPollingFirstDelay();
    }

    protected DelayFunction getDefaultPollingDelayFunction() {
        return pilot.getPollingDelayFunction();
    }

    protected ReportFunction<C> getDefaultReportFunction() {
        return (pc, text) -> pilot.getReportFunction().build(PolledComponent.generic(pc), text);
    }

    /**
     * Adds a post-action, which is executed once the action is finished
     */
    public AbstractComponentPilot<C> addPostExecution(final Consumer<C> postExec) {
        postExecutions.add(postExec);
        if (fired) {
            postExec.accept(getCachedComponent().element);
        }
        return this;
    }

    /**
     * Executes until the condition is true. This method waits for the "action delays"
     * and fires the post-executions. Use this to method to protect execution of
     * actions.
     * <p>
     * Try to override waitActionSuccessLoop instead.
     * </p>
     */
    public <V> PollingResult<C, V> waitPollingSuccess(final Polling.PollingBuilder<C, V> polling) {
        waitActionDelay();
        try (var closeable = pilot.withModalDialogDetection()) {
            final var result = waitPollingSuccessLoop(polling.build());
            if (result.isSuccess()) {
                fired = true;
                postExecutions.forEach(p -> p.accept(getCachedComponent().element));
            }
            return result;
        }
    }

    public <V, U> PollingResult<C, U> processResult(final PollingResult<C, V> result,
                                                    Function<PollingResult<C, V>, PollingResult<C, U>> resultTransformer,
                                                    @Nullable FailureHandler<C, V> onFail) {
        if (result.isSuccess()) {
            pilot.setActionDelay(result.getAndThen());
        } else {
            onFail.apply(result);
        }
        return resultTransformer.apply(result);
    }

    /**
     * Loops until the polling is successful. Can be overwritten by custom code
     *
     * @return a polling result, either successful or failure
     */
    protected <R> PollingResult<C, R> waitPollingSuccessLoop(final Polling<C, R> polling) {
        final var initializedPolling = polling.initializeFrom(this);
        return new Poller(initializedPolling.getTimeout(), initializedPolling.getFirstDelay(), initializedPolling.getDelayFunction())
                .run(p -> executePolling(p, initializedPolling), PollingResult::isSuccess).orElseThrow();
    }

    /**
     * Tries to execute the polling
     *
     * @param <R> return type
     */
    protected <R> Optional<PollingResult<C, R>> executePolling(Poller poller, final Polling<C, R>.InitializedPolling polling) {

        final var pollingFailure = loadComponent(polling);
        if (pollingFailure.isPresent()) {
            return pollingFailure.map(result -> result.withPolling(polling));
        }

        // cachedElement.element may disappear after polling, so prepare the report line
        // here
        final var logReport = polling.getReportFunction()
                .build(polling.getComponent().orElseThrow(() -> new IllegalStateException("component not set yet")),
                        polling.getReportText().orElse(null));

        logger.fine(() -> "Polling " + logReport + "...");
        final var pollingResult = callPollingFunction(polling);
        logger.fine(() -> "Polling result: " + pollingResult);
        if (pollingResult.isSuccess() && !logReport.isEmpty()) {
            pilot.getActionReport().report(logReport);
        }
        return Optional.of(pollingResult.withPolling(polling));
    }

    /**
     * Loads and check that the element is valid.
     *
     * @return a failure if the loading failed.
     */
    protected <R> Optional<PollingResult<C, R>> loadComponent(final Polling<C, R>.InitializedPolling polling) {
        if (cachedComponent == null) {
            cachedComponent = loadGuiComponent().map(LoadedComponent::new).orElse(null);
        }
        logger.fine(() -> "Cached component: " + cachedComponent);
        if (cachedComponent == null) {
            logger.fine("Not found");
            return Optional.of(PollingResults.failure("not found"));
        }
        final var polledComponent = new PolledComponent<>(this, getCachedComponent().element,
                getDescription().orElseGet(getCachedComponent().element::toString));
        polling.setComponent(polledComponent);
        final var preCondition = polling.getPrecondition();
        if (!getCachedComponent().preconditionValidated && preCondition.isPresent()
                && !preCondition.get().test(polledComponent)) {
            logger.fine("Precondition failed");
            return Optional.of(PollingResults.failure("precondition failed"));
        }
        getCachedComponent().preconditionValidated = true;
        return Optional.empty();
    }

    protected <R> PollingResult<C, R> callPollingFunction(final Polling<C, R>.InitializedPolling polling) {
        return polling.getPollingFunction().poll(polling.getComponent().orElseThrow(() -> new IllegalStateException("Component not set yet")));
    }

    /*
     * Waits on the action set by followedByDelay
     */
    protected void waitActionDelay() {
        pilot.getActionDelay().ifPresent(actionDelay -> {
            pilot.setActionDelay(null);
            actionDelay.assertFinished();
            pilot.getActionReport().report("Test delayed by: " + actionDelay);
        });
    }

}
