package ch.skymarshall.tcwriter.pilot;

import static ch.skymarshall.tcwriter.pilot.Factories.failure;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

import ch.skymarshall.tcwriter.pilot.PilotReport.ReportFunction;
import ch.skymarshall.tcwriter.pilot.PollingResult.FailureHandler;
import ch.skymarshall.util.helpers.NoExceptionCloseable;
import ch.skymarshall.util.helpers.Poller;

/**
 *
 * @author scaille
 *
 * @param <G> This type
 * @param <C> Component type
 */
public abstract class AbstractComponentPilot<G extends AbstractComponentPilot<G, C>, C> {

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

	protected AbstractComponentPilot(final GuiPilot pilot) {
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

	protected Duration getDefaultPollingTimeout() {
		return pilot.getPollingTimeout();
	}

	protected Duration getDefaultPollingFirstDelay() {
		return pilot.getPollingFirstDelay();
	}

	protected Poller.DelayFunction getDefaultPollingDelayFunction() {
		return pilot.getPollingDelayFunction();
	}

	protected ReportFunction<C> getDefaultReportFunction() {
		return (pc, text) -> pilot.getReportFunction().build(PollingContext.generic(pc), text);
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
	protected <U> U waitPollingSuccess(final Polling<C, U> polling, final FailureHandler<C, U> onFail) {

		waitActionDelay();

		try (NoExceptionCloseable closeable = pilot.withModalDialogDetection()) {
			final PollingResult<C, U> result = waitPollingSuccessLoop(polling);
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
	protected <U> PollingResult<C, U> waitPollingSuccessLoop(final Polling<C, U> polling) {
		polling.initialize(this);
		return new Poller(polling.getTimeout(), polling.getFirstDelay(), polling.getDelayFunction())
				.run(p -> executePolling(p, polling), PollingResult::isSuccess);
	}

	/**
	 * Tries to execute the polling
	 *
	 * @param <U>          return type
	 * @param precondition
	 * @param applier
	 * @return
	 */
	@SuppressWarnings("java:S1172)")
	protected <U> PollingResult<C, U> executePolling(Poller poller, final Polling<C, U> polling) {

		final Optional<PollingResult<C, U>> failure = preparePolling(polling);
		if (failure.isPresent()) {
			return failure.get();
		}

		polling.getContext().setComponent(getCachedElement(), getDescription());

		// cachedElement.element may disappear after polling, so prepare report line
		// here
		final String report = polling.getReportFunction().build(polling.getContext(), polling.getReportText());

		logger.fine(() -> "Polling " + report + "...");
		final PollingResult<C, U> result = callPollingFunction(polling);
		logger.fine(() -> "Polling result: " + result);
		if (result.isSuccess() && !report.isEmpty()) {
			pilot.getActionReport().report(report);
		}
		return result;
	}

	protected <U> Optional<PollingResult<C, U>> preparePolling(final Polling<C, U> polling) {
		if (cachedElement == null) {
			final C loadedGuiComponent = loadGuiComponent();
			if (loadedGuiComponent != null) {
				cachedElement = new LoadedElement<>(loadedGuiComponent);
			}
		}
		logger.fine(() -> "Cached component: " + cachedElement);
		if (cachedElement == null) {
			logger.fine("Not found");
			return Optional.of(failure("not found"));
		}
		if (!cachedElement.preconditionValidated && polling.getPrecondition(this) != null
				&& !polling.getPrecondition(this).test(cachedElement.element)) {
			logger.fine("Precondition failed");
			return Optional.of(failure("precondition failed"));
		}
		return Optional.empty();
	}

	protected <U> PollingResult<C, U> callPollingFunction(final Polling<C, U> polling) {
		return polling.getPollingFunction().poll(polling.getContext());
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
		return waitPollingSuccess(polling, onFail);
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

	public boolean ifEnabled(final Polling<C, Boolean> polling) {
		return waitPollingSuccess(polling, Factories.reportFailure(getDescription() + ": not found"));
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