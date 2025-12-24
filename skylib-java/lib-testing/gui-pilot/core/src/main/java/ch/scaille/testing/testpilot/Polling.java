package ch.scaille.testing.testpilot;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Predicate;

import ch.scaille.testing.testpilot.PilotReport.ReportFunction;
import ch.scaille.util.helpers.DelayFunction;
import ch.scaille.util.helpers.OverridableParameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * A polling of a component
 *
 * @param <C> type of Component
 * @param <R> type of Result
 */
@NullMarked
@Builder
@AllArgsConstructor
@Getter
public class Polling<C, R> {

    public static <C, R> Polling.PollingBuilder<C, R> of(PollingFunction<C, R> pollingFunction) {
        return Polling.<C, R>builder().pollingFunction(pollingFunction);
    }

    public static <C, R> Polling.PollingBuilder<C, R> of(Predicate<PolledComponent<C>> precondition, PollingFunction<C, R> pollingFunction) {
        return Polling.<C, R>builder().precondition(precondition).pollingFunction(pollingFunction);
    }

    public interface PollingFunction<C, R> {
        PollingResult<C, R> poll(PolledComponent<C> context);
    }

    @Nullable
    private Duration timeout;

    @Nullable
    private Duration firstDelay;

    @Nullable
    private DelayFunction delayFunction;

    /**
     * Sets a report generation function. Setting a function will make that the
     * polling is logged in the report
     */
    @Nullable
    private ReportFunction<C> reportFunction;

    @Nullable
    private final Predicate<PolledComponent<C>> precondition;

    private final PollingFunction<C, R> pollingFunction;

    /**
     * Sets the text reported in the logger. Setting a text will make that the
     * polling is logged in the report
     */
    @Nullable
    private String reportText;

    /**
     * To make that the next action will have to wait for some arbitrary delay before
     * execution
     */
    @Builder.Default
    private ActionDelay actionDelay = ActionDelay.NO_DELAY;

    private ActionDelay andThen;

    public class InitializedPolling implements PollingMetadata<C> {

        private final AbstractComponentPilot<C> componentPilot;

        @Setter
        private @Nullable PolledComponent<C> component;

        public InitializedPolling(AbstractComponentPilot<C> componentPilot) {
            this.componentPilot = componentPilot;
        }

        public Optional<Predicate<PolledComponent<C>>> getPrecondition() {
            return Optional.ofNullable(precondition);
        }

        public Duration getFirstDelay() {
            return firstDelayParam.get();
        }

        public DelayFunction getDelayFunction() {
            return delayFunctionParam.get();
        }

        public ReportFunction<C> getReportFunction() {
            return reportFunctionParam.get();
        }

        public Optional<String> getReportText() {
            return Optional.ofNullable(reportText);
        }

        @Nullable
        public ActionDelay getAndThen() {
            return andThen;
        }

        @Override
        public Duration getTimeout() {
            return actionDelay.applyOnTimeout(timeoutParam.get());
        }

        public PollingFunction<C, R> getPollingFunction() {
            return pollingFunction;
       }

        @Override
        public AbstractComponentPilot<C> getComponentPilot() {
            return componentPilot;
        }

        public Optional<PolledComponent<C>> getComponent() {
            return Optional.ofNullable(component);
        }
    }



    private final OverridableParameter<AbstractComponentPilot<C>, Duration> timeoutParam = new OverridableParameter<>(
            AbstractComponentPilot::getDefaultPollingTimeout);
    private final OverridableParameter<AbstractComponentPilot<C>, Duration> firstDelayParam = new OverridableParameter<>(
            AbstractComponentPilot::getDefaultPollingFirstDelay);
    private final OverridableParameter<AbstractComponentPilot<C>, DelayFunction> delayFunctionParam = new OverridableParameter<>(
            AbstractComponentPilot::getDefaultPollingDelayFunction);
    private final OverridableParameter<AbstractComponentPilot<C>, ReportFunction<C>> reportFunctionParam = new OverridableParameter<>(
            AbstractComponentPilot::getDefaultReportFunction);

    public InitializedPolling initializeFrom(AbstractComponentPilot<C> pilot) {
        timeoutParam.set(timeout).withSource(pilot).ensureLoaded();
        firstDelayParam.set(firstDelay).withSource(pilot).ensureLoaded();
        delayFunctionParam.set(delayFunction).withSource(pilot).ensureLoaded();
        reportFunctionParam.set(reportFunction).withSource(pilot).ensureLoaded();
        return new InitializedPolling(pilot);
    }

}
