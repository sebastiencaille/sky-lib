package ch.skymarshall.tcwriter.pilot;

import static ch.skymarshall.tcwriter.pilot.Factories.failure;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

import ch.skymarshall.tcwriter.pilot.PilotReport.ReportFunction;
import ch.skymarshall.tcwriter.pilot.Polling.PollingContext;
import ch.skymarshall.tcwriter.pilot.PollingResult.FailureHandler;
import ch.skymarshall.util.helpers.NoExceptionCloseable;
import ch.skymarshall.util.helpers.Timeout;

/**
 *
 * @author scaille
 *
 * @param <G> This type
 * @param <C> Component type
 */
public abstract class AbstractGuiComponent<G extends AbstractGuiComponent<G, C>, C> {

	private Logger logger = Logger.getLogger(getClass().getName());

	protected static class LoadedElement<TT> {
		public final TT element;
		private boolean preconditionValidated;

		public LoadedElement(final TT element) {
			this.element = element;
		}

		public boolean isPreconditionValidated() {
			return preconditionValidated;
		}

		public void setPreconditionValidated() {
			this.preconditionValidated = true;
		}

		@Override
		public String toString() {
			return "" + element + ", precond validated=" + preconditionValidated;
		}

	}

	private static ReportFunction<Object> defaultReportFunction = (pc, text) -> {
		if (text == null) {
			return "";
		}
		return pc.description + ": " + text;
	};

	public static void setDefaultReportFunction(ReportFunction<Object> defaultReportFunction) {
		AbstractGuiComponent.defaultReportFunction = defaultReportFunction;
	}

	/**
	 * Loads a component from the gui
	 *
	 * @return
	 */
	protected abstract C loadGuiComponent();

	/**
	 * Checks if a component is in a state that allows checking it's state
	 *
	 * @param component
	 * @return
	 */
	protected abstract boolean canCheck(final C component);

	/**
	 * Checks if a component is in a state that allows edition
	 *
	 * @param component
	 * @return
	 */
	protected abstract boolean canEdit(final C component);

	private final GuiPilot pilot;

	private final List<Consumer<C>> postExecutions = new ArrayList<>();

	private LoadedElement<C> cachedElement = null;

	protected boolean fired = false;

	protected AbstractGuiComponent(final GuiPilot pilot) {
		this.pilot = pilot;
	}

	protected String getDescription() {
		if (getCachedElement() != null) {
			return getCachedElement().toString();
		}
		return null;
	}

	public C getCachedElement() {
		if (cachedElement == null) {
			return null;
		}
		return cachedElement.element;
	}

	protected void invalidateCache() {
		if (fired) {
			throw new IllegalStateException("Action was already fired");
		}
		cachedElement = null;
	}

	protected Duration pollingTime(final Duration duration) {
		if (duration.toMillis() < 500) {
			return Duration.ofMillis(50);
		} else if (duration.toMillis() < 10_000) {
			return Duration.ofMillis(250);
		} else if (duration.toMillis() < 60_000) {
			return Duration.ofMillis(1_000);
		}
		return Duration.ofMillis(5_000);
	}

	/**
	 * Adds a post-action, which is executed once action is finished
	 *
	 * @param postExec
	 */
	public G addPostExecution(final Consumer<C> postExec) {
		postExecutions.add(postExec);
		if (fired) {
			postExec.accept(cachedElement.element);
		}
		return (G) this;
	}

	/**
	 * Executes until condition is true. This method waits for the "action delays"
	 * and fires the post executions. Use this to method to protect execution of
	 * actions.
	 *
	 * Prefer overriding waitActionSuccessLoop
	 *
	 * @param condition
	 * @param applier
	 * @param timeout
	 * @return
	 */
	protected <U> U waitPollingSuccess(final Polling<C, U> polling, final Duration timeout,
			final FailureHandler<C, U> onFail) {

		waitActionDelay();

		try (NoExceptionCloseable closeable = pilot.withModalDialogDetection()) {
			final PollingResult<C, U> result = waitPollingSuccessLoop(polling, timeout);
			if (result.isSuccess()) {
				fired = true;
				postExecutions.stream().forEach(p -> p.accept(cachedElement.element));
				pilot.setActionDelay(polling.getActionDelay());
			}

			result.setInformation(toString(), cachedElement);
			return result.orElseGet(() -> onFail.apply(result, pilot));
		}
	}

	/**
	 * Loops until the polling is successful. Can be overwritten by custom code
	 *
	 * @param <U>          return type
	 * @param precondition a precondition
	 * @param applier      action applied on component
	 * @param reporting    reporting, if action is successful
	 * @param timeout
	 * @return a polling result, either successful or failure
	 */
	protected <U> PollingResult<C, U> waitPollingSuccessLoop(final Polling<C, U> polling, final Duration timeout) {
		final Timeout timeoutCheck = new Timeout(timeout);
		PollingResult<C, U> lastResult = failure("No information");
		while (!timeoutCheck.hasTimedOut()) {
			lastResult = executePolling(polling);
			if (lastResult.isSuccess()) {
				break;
			}
			try {
				Thread.sleep(pollingTime(timeout).toMillis());
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
				return Factories.failure("Interrupted");
			}
		}
		return lastResult;
	}

	/**
	 * Tries to execute the polling
	 *
	 * @param <U>          return type
	 * @param precondition
	 * @param applier
	 * @return
	 */
	protected <U> PollingResult<C, U> executePolling(final Polling<C, U> polling) {

		if (cachedElement == null) {
			final C loadedGuiComponent = loadGuiComponent();
			if (loadedGuiComponent != null) {
				cachedElement = new LoadedElement<>(loadedGuiComponent);
			}
		}
		logger.fine(() -> "Cached component: " + cachedElement);
		if (cachedElement == null) {
			logger.fine("Not found");
			return failure("not found");
		}
		if (!cachedElement.preconditionValidated && polling.getPrecondition(this) != null
				&& !polling.getPrecondition(this).test(cachedElement.element)) {
			logger.fine("Precondition failed");
			return failure("precondition failed");
		}

		PollingContext<C> pollingContext = new PollingContext<>(cachedElement.element, getDescription());

		// cachedElement.element may disappear after polling, so prepare report line
		// here
		final String report = polling.getReportFunction().orElse(getDefaultReportFunction()).build(pollingContext,
				polling.getReportText());

		logger.fine(() -> "Polling " + report + "...");
		final PollingResult<C, U> result = polling.getPollingFunction().poll(pollingContext);
		logger.fine(() -> "Polling result: " + result);
		if (result.isSuccess() && !report.isEmpty()) {
			pilot.getActionReport().report(report);
		}
		return result;
	}

	protected ReportFunction<C> getDefaultReportFunction() {
		return (pc, text) -> defaultReportFunction.build(new PollingContext<>(pc.component, pc.description), text);
	}

	protected String reportNameOf(C c) {
		return c.toString();
	}

	/**
	 * Waits until a component is checked/edited
	 *
	 * @param <U>     returned value type
	 * @param polling check/edition
	 * @param onFail  action performed on failure
	 * @return check/edition value
	 */
	public <U> U wait(final Polling<C, U> polling, final FailureHandler<C, U> onFail) {
		return waitPollingSuccess(polling, pilot.getDefaultActionTimeout(), onFail);
	}

	/**
	 * Waits until a component is edited, throwing a java assertion error in case of
	 * failure
	 *
	 * @param <U>     return type
	 * @param polling
	 * @return
	 */
	public <U> U wait(final Polling<C, U> polling) {
		return wait(polling, Factories.throwError());
	}

	/**
	 * Waits until a component is checked, throwing an assertion error if the check
	 *
	 * @param <U>     returned value type
	 * @param polling check/edition
	 * @return check/edition value
	 */
	public boolean wait(Predicate<C> check, String report) {
		return wait(Factories.satisfies(check).withReportText(report));
	}

	public boolean ifEnabled(final Polling<C, Boolean> polling, final Duration shortTimeout) {
		return waitPollingSuccess(polling, shortTimeout, Factories.reportFailure(getDescription() + ": not found"));
	}

	/**
	 * Waits on the action set by followedByDelay
	 */
	protected void waitActionDelay() {
		final ActionDelay actionDelay = pilot.getActionDelay();
		if (actionDelay != null) {
			pilot.setActionDelay(null);
			actionDelay.waitFinished();
			pilot.getActionReport().report("Test delayed by: " + actionDelay);
		}
	}

	/**
	 * @See Factories.action
	 */
	public Polling<C, Boolean> action(final Consumer<C> action) {
		return Factories.action(action);
	}

	/**
	 * @See Factories.assertion
	 */
	public Polling<C, Boolean> assertion(final Consumer<PollingContext<C>> assertion) {
		return Factories.assertion(assertion);
	}

	/**
	 * @See Factories.satisfies
	 */
	public Polling<C, Boolean> satisfies(final Predicate<C> predicate) {
		return Factories.satisfies(predicate);
	}

}
