package ch.scaille.tcwriter.pilot;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Predicate;

import ch.scaille.tcwriter.pilot.PilotReport.ReportFunction;
import ch.scaille.util.helpers.OverridableParameter;
import ch.scaille.util.helpers.Poller;

/**
 * A polling of a component
 * @param <C> type of component
 * @param <V> type of returned value
 */
public class Polling<C, V> {

	public interface PollingFunction<C, V> {
		PollingResult<C, V> poll(PollingContext<C> context);
	}

	private final Predicate<C> precondition;

	private final PollingFunction<C, V> pollingFunction;

	private final OverridableParameter<AbstractComponentPilot<?, C>, Duration> timeout = new OverridableParameter<>(
			AbstractComponentPilot::getDefaultPollingTimeout);
	private final OverridableParameter<AbstractComponentPilot<?, C>, Duration> firstDelay = new OverridableParameter<>(
			AbstractComponentPilot::getDefaultPollingFirstDelay);
	private final OverridableParameter<AbstractComponentPilot<?, C>, Poller.DelayFunction> delayFunction = new OverridableParameter<>(
			AbstractComponentPilot::getDefaultPollingDelayFunction);
	private final OverridableParameter<AbstractComponentPilot<?, C>, ReportFunction<C>> reportFunction = new OverridableParameter<>(
			AbstractComponentPilot::getDefaultReportFunction);

	private String reportText = null;

	private ActionDelay actionDelay = null;

	private PollingContext<C> context = null;

	private ActionDelay currentDelay;

	public Polling(final Predicate<C> precondition, final PollingFunction<C, V> pollingFunction) {
		this.precondition = precondition;
		this.pollingFunction = pollingFunction;
	}

	public PollingContext<C> getContext() {
		return context;
	}

	public Optional<Predicate<C>> getPrecondition(final AbstractComponentPilot<?, C> guiComponent) {
		return Optional.ofNullable(precondition);
	}

	public PollingFunction<C, V> getPollingFunction() {
		return pollingFunction;
	}

	public Duration getFirstDelay() {
		return firstDelay.get();
	}

	public Poller.DelayFunction getDelayFunction() {
		return delayFunction.get();
	}

	public ReportFunction<C> getReportFunction() {
		return reportFunction.get();
	}

	public String getReportText() {
		return reportText;
	}

	public ActionDelay getActionDelay() {
		return actionDelay;
	}

	public Polling<C, V> withTimeout(Duration timeout) {
		this.timeout.set(timeout);
		return this;
	}

	public Polling<C, V> withFirstDelay(Duration initialDelay) {
		this.firstDelay.set(initialDelay);
		return this;
	}

	public Polling<C, V> withDelay(Duration delay) {
		this.delayFunction.set(t -> delay);
		return this;
	}

	public Polling<C, V> withDelayFunction(Poller.DelayFunction delay) {
		this.delayFunction.set(delay);
		return this;
	}

	/**
	 * Sets a report generation function. Setting a function will make that the
	 * polling is logged in the report
	 * 
	 * @param name
	 * @return
	 */
	public Polling<C, V> withReportFunction(ReportFunction<C> reportFunction) {
		this.reportFunction.set(reportFunction);
		return this;
	}

	/**
	 * Sets the report text. Setting a text will make that the polling is logged in
	 * the report
	 * 
	 * @param name
	 * @return
	 */
	public Polling<C, V> withReportText(final String reportText) {
		this.reportText = reportText;
		return this;
	}

	public void withExtraDelay(ActionDelay currentDelay) {
		this.currentDelay = currentDelay;
	}

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
	 * @param actionDelay
	 * @return
	 */
	public Polling<C, V> followedBy(final ActionDelay actionDelay) {
		this.actionDelay = actionDelay;
		return this;
	}

	public Polling<C, V> initialize(AbstractComponentPilot<?, C> component) {
		timeout.withSource(component).ensureLoaded();
		firstDelay.withSource(component).ensureLoaded();
		delayFunction.withSource(component).ensureLoaded();
		reportFunction.withSource(component).ensureLoaded();
		context = new PollingContext<>();
		return this;
	}

}
