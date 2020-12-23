package ch.skymarshall.tcwriter.pilot;

import static ch.skymarshall.tcwriter.pilot.PollingResult.failure;
import static ch.skymarshall.tcwriter.pilot.PollingResult.throwError;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import ch.skymarshall.tcwriter.pilot.PollingResult.PollingResultFunction;
import ch.skymarshall.util.helpers.NoExceptionCloseable;

/**
 *
 * @author scaille
 *
 * @param <G> This type
 * @param <C> Component type
 */
public abstract class AbstractGuiComponent<G extends AbstractGuiComponent<G, C>, C> {

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
			final PollingResultFunction<C, U> onFail) {

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
		final long startTime = System.currentTimeMillis();
		PollingResult<C, U> lastResult = failure("No information");
		while (System.currentTimeMillis() - startTime < timeout.toMillis()) {
			lastResult = executePolling(polling);
			if (lastResult.isSuccess()) {
				break;
			}
			try {
				Thread.sleep(pollingTime(timeout).toMillis());
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
				return failure("Interrupted");
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
		if (cachedElement == null) {
			return failure("not found");
		}
		if (!cachedElement.preconditionValidated && polling.getPrecondition(this) != null
				&& !polling.getPrecondition(this).test(cachedElement.element)) {
			return failure("precondition failed");
		}

		final String report = polling.getReportLine().apply(cachedElement.element); // element may disappear after
																					// action
		final PollingResult<C, U> result = polling.getPollingFunction().poll(cachedElement.element);
		if (result.isSuccess() && !report.isEmpty()) {
			pilot.getActionReport().report(report);
		}
		return result;
	}

	/**
	 * Waits until a component is edited
	 *
	 * @param <U>     return type
	 * @param polling
	 * @param onFail
	 * @return
	 */
	public <U> U wait(final Polling<C, U> polling, final PollingResultFunction<C, U> onFail) {
		return waitPollingSuccess(polling, pilot.getDefaultActionTimeout(), onFail);
	}

	/**
	 * Waits until a component is edited, throwing a java assertion error if edition
	 * failed
	 *
	 * @param <U>     return type
	 * @param polling
	 * @return
	 */
	public <U> U wait(final Polling<C, U> polling) {
		return wait(polling, throwError());
	}

	/**
	 * Waits on the action set by followedByDelay
	 */
	protected void waitActionDelay() {
		final ActionDelay actionDelay = pilot.getActionDelay();
		if (actionDelay != null) {
			pilot.setActionDelay(null);
			actionDelay.waitFinished();
		}
	}

	/**
	 * @See EditionPolling.action
	 */
	public Polling<C, Boolean> action(final Consumer<C> action) {
		return EditionPolling.action(action);
	}

	/**
	 * @See StatePolling.assertion
	 */
	public Polling<C, Boolean> assertion(final Consumer<C> assertion) {
		return StatePolling.assertion(assertion);
	}

	/**
	 * @See StatePolling.satisfies
	 */
	public Polling<C, Boolean> satisfies(final Predicate<C> predicate) {
		return StatePolling.satisfies(predicate);
	}

}
