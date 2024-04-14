package ch.scaille.tcwriter.pilot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

import ch.scaille.tcwriter.pilot.PilotReport.ReportFunction;
import ch.scaille.tcwriter.pilot.PollingResult.FailureHandler;
import ch.scaille.tcwriter.pilot.factories.PollingResults;
import ch.scaille.util.helpers.Logs;
import ch.scaille.util.helpers.Poller;

/**
 * Class that allows to poll graphical components
 *
 * @param <P> Type of this Pilot
 * @param <C> Component type
 */
public abstract class AbstractComponentPilot<P extends AbstractComponentPilot<P, C>, C> {

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
	protected abstract boolean canCheck(final PollingContext<C> ctxt);

	private final GuiPilot pilot;

	private final List<Consumer<C>> postExecutions = new ArrayList<>();

	private LoadedComponent<C> cachedComponent = null;

	protected boolean fired = false;

	protected AbstractComponentPilot(final GuiPilot pilot) {
		this.pilot = pilot;
	}

	public GuiPilot getPilot() {
		return pilot;
	}

	protected Optional<String> getDescription() {
		return getCachedElement().map(Object::toString);
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
	public P addPostExecution(final Consumer<C> postExec) {
		postExecutions.add(postExec);
		if (fired) {
			postExec.accept(cachedComponent.element);
		}
		return (P) this;
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
	public <V> PollingResult<C, V> waitPollingSuccess(final Polling<C, V> polling) {
		polling.withExtraDelay(pilot.getActionDelay());
		waitActionDelay();
		try (var closeable = pilot.withModalDialogDetection()) {
			final var result = waitPollingSuccessLoop(polling);
			result.setPolling(polling);
			if (result.isSuccess()) {
				fired = true;
				postExecutions.stream().forEach(p -> p.accept(cachedComponent.element));
			}
			return result;
		}
	}

	public <V, U> U processResult(final PollingResult<C, V> result, Function<V, U> successTransformer,
			FailureHandler<C, V, U> onFail) {
		if (result.isSuccess()) {
			pilot.setActionDelay(result.getPolling().getActionDelay());
		}
		return result.mapOrGet(successTransformer, () -> onFail.apply(result, pilot));
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
	protected <R> Optional<PollingResult<C, R>> executePolling(Poller poller, final Polling<C, R> polling) {

		final var pollingFailure = loadComponent(polling);
		if (pollingFailure.isPresent()) {
			return pollingFailure;
		}

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
			polling.getContext().setComponent(null, getDescription().orElse("<unknown>"));
			return Optional.of(PollingResults.failure("not found"));
		}
		polling.getContext()
				.setComponent(getCachedElement().orElse(null),
						getDescription().or(() -> getCachedElement().map(Object::toString)).orElse("<unknown>"));
		final var preCondition = polling.getPrecondition();
		if (!cachedComponent.preconditionValidated && preCondition.isPresent()
				&& !preCondition.get().test(polling.getContext())) {
			logger.fine("Precondition failed");
			return Optional.of(PollingResults.failure("precondition failed"));
		}
		return Optional.empty();
	}

	protected <R> PollingResult<C, R> callPollingFunction(final Polling<C, R> polling) {
		return polling.getPollingFunction().poll(polling.getContext());
	}

	/*
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

	public PollingBuilder<P, C> polling() {
		return new PollingBuilder<>(this);
	}
}
