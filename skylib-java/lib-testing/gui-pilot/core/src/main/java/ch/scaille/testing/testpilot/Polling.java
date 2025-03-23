package ch.scaille.testing.testpilot;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Predicate;

import ch.scaille.testing.testpilot.PilotReport.ReportFunction;
import ch.scaille.util.helpers.OverridableParameter;
import ch.scaille.util.helpers.Poller;

/**
 * A polling of a component
 * 
 * @param <C> type of Component
 * @param <R> type of Result
 */
public class Polling<C, R> implements PollingMetadata<C> {

	public interface PollingFunction<C, R> {
		PollingResult<C, R> poll(PollingContext<C> context);
	}

	private final OverridableParameter<AbstractComponentPilot<C>, Duration> timeout = new OverridableParameter<>(
			AbstractComponentPilot::getDefaultPollingTimeout);
	private final OverridableParameter<AbstractComponentPilot<C>, Duration> firstDelay = new OverridableParameter<>(
			AbstractComponentPilot::getDefaultPollingFirstDelay);
	private final OverridableParameter<AbstractComponentPilot<C>, Poller.DelayFunction> delayFunction = new OverridableParameter<>(
			AbstractComponentPilot::getDefaultPollingDelayFunction);
	private final OverridableParameter<AbstractComponentPilot<C>, ReportFunction<C>> reportFunction = new OverridableParameter<>(
			AbstractComponentPilot::getDefaultReportFunction);

	private final Predicate<PollingContext<C>> precondition;

	private final PollingFunction<C, R> pollingFunction;

	private String reportText = null;

	private ActionDelay actionDelay = null;

	private PollingContext<C> context = null;

	private ActionDelay currentDelay;

	public Polling(PollingFunction<C, R> pollingFunction) {
		this(null, pollingFunction);
	}

	public Polling(final Predicate<PollingContext<C>> precondition, final PollingFunction<C, R> pollingFunction) {
		this.precondition = precondition;
		this.pollingFunction = pollingFunction;
	}

	@Override
	public PollingContext<C> getContext() {
		return context;
	}

	public Optional<Predicate<PollingContext<C>>> getPrecondition() {
		return Optional.ofNullable(precondition);
	}

	public PollingFunction<C, R> getPollingFunction() {
		return pollingFunction;
	}

	@Override
	public Duration getFirstDelay() {
		return firstDelay.get();
	}

	@Override
	public Poller.DelayFunction getDelayFunction() {
		return delayFunction.get();
	}

	@Override
	public ReportFunction<C> getReportFunction() {
		return reportFunction.get();
	}

	@Override
	public String getReportText() {
		return reportText;
	}

	@Override
	public ActionDelay getActionDelay() {
		return actionDelay;
	}

	public Polling<C, R> withTimeout(Duration timeout) {
		this.timeout.set(timeout);
		return this;
	}

	public Polling<C, R> withFirstDelay(Duration initialDelay) {
		this.firstDelay.set(initialDelay);
		return this;
	}

	public Polling<C, R> withDelay(Duration delay) {
		this.delayFunction.set(t -> delay);
		return this;
	}

	public Polling<C, R> withDelayFunction(Poller.DelayFunction delay) {
		this.delayFunction.set(delay);
		return this;
	}

	public Polling<C, R> withExtraDelay(ActionDelay currentDelay) {
		this.currentDelay = currentDelay;
		return this;
	}

	/**
	 * Sets a report generation function. Setting a function will make that the
	 * polling is logged in the report
	 */
	public Polling<C, R> withReportFunction(ReportFunction<C> reportFunction) {
		this.reportFunction.set(reportFunction);
		return this;
	}

	/**
	 * Sets the text reported in the logger. Setting a text will make that the
	 * polling is logged in the report
	 */
	public Polling<C, R> withReportText(final String reportText) {
		this.reportText = reportText;
		return this;
	}

	@Override
	public Duration getTimeout() {
		var effectiveTimeout = timeout.get();
		if (currentDelay != null) {
			effectiveTimeout = currentDelay.applyOnTimeout(effectiveTimeout);
		}
		return effectiveTimeout;
	}

	/**
	 * To say that the next action will have to wait for some arbitrary delay before
	 * execution
	 *
     */
	public Polling<C, R> andThen(final ActionDelay actionDelay) {
		this.actionDelay = actionDelay;
		return this;
	}

	public Polling<C, R> initializeFrom(AbstractComponentPilot<C> pilot) {
		timeout.withSource(pilot).ensureLoaded();
		firstDelay.withSource(pilot).ensureLoaded();
		delayFunction.withSource(pilot).ensureLoaded();
		reportFunction.withSource(pilot).ensureLoaded();
		context = new PollingContext<>(pilot);
		return this;
	}

}
