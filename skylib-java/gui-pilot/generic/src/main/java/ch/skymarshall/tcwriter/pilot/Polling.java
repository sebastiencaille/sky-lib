package ch.skymarshall.tcwriter.pilot;

import java.util.Optional;
import java.util.function.Predicate;

import ch.skymarshall.tcwriter.pilot.PilotReport.ReportFunction;

public class Polling<C, V> {

	public static class PollingContext<C> {
		public final C component;
		public final String description;

		public PollingContext(C component, String description) {
			this.component = component;
			this.description = description;
		}
	}

	public interface PollingFunction<C, V> {
		PollingResult<C, V> poll(PollingContext<C> context);
	}

	private final Predicate<C> precondition;

	private final PollingFunction<C, V> pollingFunction;

	private Optional<ReportFunction<C>> reportFunction = Optional.empty();

	private String reportText = null;

	private ActionDelay actionDelay = null;

	public Polling(final Predicate<C> precondition, final PollingFunction<C, V> pollingFunction) {
		this.precondition = precondition;
		this.pollingFunction = pollingFunction;
	}

	public Predicate<C> getPrecondition(final AbstractGuiComponent<?, C> guiComponent) {
		return precondition;
	}

	public PollingFunction<C, V> getPollingFunction() {
		return pollingFunction;
	}

	public Optional<ReportFunction<C>> getReportFunction() {
		return reportFunction;
	}

	/**
	 * Sets a report generation function. Setting a function will make that the
	 * polling is logged in the report
	 * 
	 * @param name
	 * @return
	 */
	public Polling<C, V> withReportFunction(ReportFunction<C> reportFunction) {
		this.reportFunction = Optional.of(reportFunction);
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

	public String getReportText() {
		return reportText;
	}

	public ActionDelay getActionDelay() {
		return actionDelay;
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

}
