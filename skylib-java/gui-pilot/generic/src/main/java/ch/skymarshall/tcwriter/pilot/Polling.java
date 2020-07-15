package ch.skymarshall.tcwriter.pilot;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Polling<C, V> {

	public interface PollingFunction<C, V> {
		PollingResult<C, V> poll(C component);
	}

	private final Predicate<C> precondition;

	private final PollingFunction<C, V> pollingFunction;

	private Function<C, String> reportLine;

	private String name = null;

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

	public Function<C, String> getReportLine() {
		if (reportLine == null && name != null) {
			return c -> c.toString() + ": " + name;
		} else if (reportLine == null) {
			return c -> "";
		}
		return reportLine;
	}

	public Polling<C, V> withReport(final Function<C, String> reportLine) {
		this.reportLine = reportLine;
		return this;
	}

	public Polling<C, V> withName(final String name) {
		this.name = name;
		return this;
	}

	public static <C> Polling<C, Boolean> success(final Consumer<C> action) {
		return new Polling<>(null, c -> {
			action.accept(c);
			return PollingResult.success();
		});
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

	/**
	 * No precondition tested
	 *
	 * @return a precondition that is always true
	 */
	public static <C> Predicate<C> none() {
		return p -> true;
	}

}
