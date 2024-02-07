package ch.scaille.tcwriter.pilot;

import static ch.scaille.tcwriter.pilot.Factories.PollingResults.failure;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

import ch.scaille.tcwriter.pilot.Factories.FailureHandlers;
import ch.scaille.tcwriter.pilot.Factories.Pollings;
import ch.scaille.tcwriter.pilot.PilotReport.ReportFunction;
import ch.scaille.tcwriter.pilot.PollingResult.FailureHandler;
import ch.scaille.util.helpers.Logs;
import ch.scaille.util.helpers.Poller;

/**
 * Class that allows to poll graphical components
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
	protected <P, R> R waitPollingSuccess(final Polling<C, P> polling, final Function<P, R> successTransformer,
			final FailureHandler<C, P, R> onFail) {
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
			return result.mapOrGet(successTransformer, () -> onFail.apply(result, pilot));
		}
	}

	/**
	 * Loops until the polling is successful. Can be overwritten by custom code
	 *
	 * @param <R>          return type
	 * @param precondition a precondition
	 * @param applier      action applied on component
	 * @param reporting    reporting, if action is successful
	 * @param timeout
	 * @return a polling result, either successful or failure
	 */
	protected <R> PollingResult<C, R> waitPollingSuccessLoop(final Polling<C, R> polling) {
		polling.initialize(this);
		return new Poller(polling.getTimeout(), polling.getFirstDelay(), polling.getDelayFunction())
				.run(p -> executePolling(p, polling), PollingResult::isSuccess)
				.orElseThrow();
	}

	/**
	 * Tries to execute the polling
	 *
	 * @param <R>          return type
	 * @param precondition
	 * @param applier
	 * @return
	 */
	@SuppressWarnings("java:S1172)")
	protected <R> Optional<PollingResult<C, R>> executePolling(Poller poller, final Polling<C, R> polling) {

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
	 * @param <R>
	 * @param polling
	 * @return
	 */
	protected <R> Optional<PollingResult<C, R>> loadComponent(final Polling<C, R> polling) {
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

	protected <R> PollingResult<C, R> callPollingFunction(final Polling<C, R> polling) {
		return polling.getPollingFunction().poll(polling.getContext());
	}

	public class Wait<R> {

		protected final Polling<C, R> polling;

		public Wait(Polling<C, R> polling) {
			this.polling = polling;
		}

		/**
		 * Waits until a component is checked/edited
		 * 
		 * @param onFail failure behavior
		 */
		public R or(final FailureHandler<C, R, R> onFail) {
			return waitPollingSuccess(polling, r -> r, onFail);
		}

		/**
		 * Waits until a component is edited, throwing a java assertion error in case of
		 * failure
		 */
		public R orFail() {
			return or(FailureHandlers.throwError());
		}

		public R orFail(String report) {
			return waitPollingSuccess(polling.withReportText(report), r -> r, FailureHandlers.throwError());
		}

		public boolean isSatisfied() {
			return AbstractComponentPilot.this
					.waitPollingSuccess(polling, r -> Boolean.TRUE, FailureHandlers.ignoreFailure())
					.booleanValue();
		}

		public boolean isSatisfiedOr(String report) {
			return AbstractComponentPilot.this
					.waitPollingSuccess(polling.withReportText(report), r -> Boolean.TRUE,
							FailureHandlers.reportNotSatisfied(report))
					.booleanValue();
		}
	}


	public <U> Wait<U> polling(final Polling<C, U> polling) {
		return new Wait<>(polling);
	}

	/**
	 * Waits until a component is checked, throwing an assertion error if the check
	 *
	 * @param <U>     returned value type
	 * @param polling check/edition
	 * @return check/edition value
	 */
	public Wait<Boolean> polling(Predicate<C> check) {
		return new Wait<>(Factories.Pollings.satisfies(check));
	}

	/**
	 * Waits on the action set by followedByDelay
	 */
	protected void waitActionDelay() {
		final var actionDelay = pilot.getActionDelay();
		if (actionDelay != null) {
			pilot.setActionDelay(null);
			actionDelay.assertFinished();
			pilot.getActionReport().report("Test delayed by: " + actionDelay);
		}
	}

	/**
	 * Useful to avoid Generic issues
	 * @See Factories.action
	 */
	public Polling<C, Boolean> applies(final Consumer<C> action) {
		return Pollings.applies(action);
	}

	/**
	 * @See Factories.assertion
	 */
	public Polling<C, Boolean> asserts(final Consumer<PollingContext<C>> assertion) {
		return Pollings.asserts(assertion);
	}

	/**
	 * @See Factories.satisfies
	 */
	public Polling<C, Boolean> satisfies(final Predicate<C> predicate) {
		return Pollings.satisfies(predicate);
	}

}
