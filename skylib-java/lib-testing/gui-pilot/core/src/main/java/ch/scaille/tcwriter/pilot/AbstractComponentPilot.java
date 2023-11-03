package ch.scaille.tcwriter.pilot;

import static ch.scaille.tcwriter.pilot.Factories.PollingResults.failure;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

import ch.scaille.tcwriter.pilot.Factories.FailureHandlers;
import ch.scaille.tcwriter.pilot.Factories.Pollings;
import ch.scaille.tcwriter.pilot.PilotReport.ReportFunction;
import ch.scaille.tcwriter.pilot.PollingResult.FailureHandler;
import ch.scaille.util.helpers.Logs;
import ch.scaille.util.helpers.Poller;

/**
 *
 * @author scaille
 *
 * @param <G> This type
 * @param <C> Component type
 */
public abstract class AbstractComponentPilot<G extends AbstractComponentPilot<G, C>, C> {

	private final Logger logger = Logs.of(this);

	protected static class LoadedComponent<T> {
		public final T element;
		private boolean preconditionValidated;

		public LoadedComponent(final T element) {
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
	protected abstract Optional<C> loadGuiComponent();

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

	private LoadedComponent<C> cachedComponent = null;

	protected boolean fired = false;

	protected AbstractComponentPilot(final GuiPilot pilot) {
		this.pilot = pilot;
	}

	protected String getDescription() {
		return getCachedElement().map(Object::toString).orElse(null);
	}

	public Optional<C> getCachedElement() {
		if (cachedComponent == null) {
			return Optional.empty();
		}
		return Optional.of(cachedComponent.element);
	}

	protected void invalidateCache() {
		if (fired) {
			throw new IllegalStateException("Action was already fired");
		}
		cachedComponent = null;
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
			postExec.accept(cachedComponent.element);
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
		polling.withExtraDelay(pilot.getActionDelay());
		waitActionDelay();
		try (var closeable = pilot.withModalDialogDetection()) {
			final var result = waitPollingSuccessLoop(polling);
			if (result.isSuccess()) {
				fired = true;
				postExecutions.stream().forEach(p -> p.accept(cachedComponent.element));
				pilot.setActionDelay(polling.getActionDelay());
			}

			result.setInformation(toString(), cachedComponent);
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
				.run(p -> executePolling(p, polling), PollingResult::isSuccess)
				.orElseThrow();
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
	protected <U> Optional<PollingResult<C, U>> executePolling(Poller poller, final Polling<C, U> polling) {

		final var pollingFailure = loadComponent(polling);
		if (pollingFailure.isPresent()) {
			return pollingFailure;
		}

		polling.getContext().setComponent(getCachedElement().orElse(null), getDescription());

		// cachedElement.element may disappear after polling, so prepare report line
		// here
		final var logReport = polling.getReportFunction().build(polling.getContext(), polling.getReportText());

		logger.fine(() -> "Polling " + logReport + "...");
		final var pollingResult = callPollingFunction(polling);
		logger.fine(() -> "Polling result: " + pollingResult);
		if (pollingResult.isSuccess() && !logReport.isEmpty()) {
			pilot.getActionReport().report(logReport);
		}
		return Optional.of(pollingResult);
	}

	/**
	 * Loads and check that the element is valid
	 * 
	 * @param <U>
	 * @param polling
	 * @return
	 */
	protected <U> Optional<PollingResult<C, U>> loadComponent(final Polling<C, U> polling) {
		if (cachedComponent == null) {
			cachedComponent = loadGuiComponent().map(LoadedComponent::new).orElse(null);
		}
		logger.fine(() -> "Cached component: " + cachedComponent);
		if (cachedComponent == null) {
			logger.fine("Not found");
			return Optional.of(failure("not found"));
		}
		final var preCondition = polling.getPrecondition(this);
		if (!cachedComponent.preconditionValidated && preCondition.isPresent()
				&& !preCondition.get().test(cachedComponent.element)) {
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
		return wait(polling, FailureHandlers.throwError());
	}

	/**
	 * Waits until a component is checked, throwing an assertion error if the check
	 *
	 * @param <U>     returned value type
	 * @param polling check/edition
	 * @return check/edition value
	 */
	public boolean wait(Predicate<C> check, String report) {
		return wait(Factories.Pollings.satisfies(check).withReportText(report));
	}

	public boolean ifEnabled(final Polling<C, Boolean> polling) {
		return waitPollingSuccess(polling, FailureHandlers.reportNotFound(getDescription() + ": not found"));
	}

	/**
	 * Waits on the action set by followedByDelay
	 */
	protected void waitActionDelay() {
		final var actionDelay = pilot.getActionDelay();
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
		return Pollings.action(action);
	}

	/**
	 * @See Factories.assertion
	 */
	public Polling<C, Boolean> assertion(final Consumer<PollingContext<C>> assertion) {
		return Pollings.assertion(assertion);
	}

	/**
	 * @See Factories.satisfies
	 */
	public Polling<C, Boolean> satisfies(final Predicate<C> predicate) {
		return Pollings.satisfies(predicate);
	}

}
