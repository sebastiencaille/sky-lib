package ch.skymarshall.tcwriter.pilot;

import static ch.skymarshall.tcwriter.pilot.Polling.failure;
import static ch.skymarshall.tcwriter.pilot.Polling.throwError;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import ch.skymarshall.tcwriter.pilot.Polling.PollingFunction;
import ch.skymarshall.tcwriter.pilot.Polling.PollingResultFunction;
import ch.skymarshall.util.helpers.NoExceptionCloseable;

public abstract class AbstractGuiComponent<C extends AbstractGuiComponent<C, T>, T> {

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
	protected abstract T loadGuiComponent();

	/**
	 * Checks if a component is in a state that allows checking it's state
	 *
	 * @param component
	 * @return
	 */
	protected abstract boolean canCheck(final T component);

	/**
	 * Checks if a component is in a state that allows edition
	 *
	 * @param component
	 * @return
	 */
	protected abstract boolean canEdit(final T component);

	private final GuiPilot pilot;

	private final List<Consumer<T>> postExecutions = new ArrayList<>();

	private LoadedElement<T> cachedElement = null;

	protected boolean fired = false;

	/** Next report line */
	private Function<T, String> reportLine = t -> null;

	public AbstractGuiComponent(final GuiPilot pilot) {
		this.pilot = pilot;
	}

	public T getCachedElement() {
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
	public C addPostExecution(final Consumer<T> postExec) {
		postExecutions.add(postExec);
		if (fired) {
			postExec.accept(cachedElement.element);
		}
		return (C) this;
	}

	public C withReport(final Function<T, String> reportLine) {
		this.reportLine = t -> toString() + ": " + reportLine.apply(t);
		return (C) this;
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
	protected <U> U waitActionSuccess(final Predicate<T> precondition, final PollingFunction<T, U> applier,
			final Duration timeout, final PollingResultFunction<T, U> onFail) {

		waitActionDelay();

		try (NoExceptionCloseable closeable = pilot.withModalDialogDetection()) {
			final Polling<T, U> result = waitActionSuccessLoop(precondition, applier, timeout);
			if (result.isSuccess()) {
				fired = true;
				postExecutions.stream().forEach(p -> p.accept(cachedElement.element));
			}

			result.setInformation(pilot, toString(), cachedElement);
			final U resultWithFail = result.orElseGet(() -> onFail.apply(result));

			reportLine = null;
			return resultWithFail;
		}
	}

	/**
	 * Loops until the action is processed. Can be overwritten by custom code
	 *
	 * @param <U>          return type
	 * @param precondition a precondition
	 * @param applier      action applied on component
	 * @param reporting    reporting, if action is successful
	 * @param timeout
	 * @return a polling result, either successful or failure
	 */
	protected <U> Polling<T, U> waitActionSuccessLoop(final Predicate<T> precondition,
			final PollingFunction<T, U> applier, final Duration timeout) {
		final long startTime = System.currentTimeMillis();
		Polling<T, U> lastResult = failure("No information");
		while (System.currentTimeMillis() - startTime < timeout.toMillis()) {
			lastResult = executePolling(precondition, applier);
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
	 * Tries to execute the action
	 *
	 * @param <U>          return type
	 * @param precondition
	 * @param applier
	 * @return
	 */
	protected <U> Polling<T, U> executePolling(final Predicate<T> precondition, final PollingFunction<T, U> applier) {

		if (cachedElement == null) {
			final T loadedGuiComponent = loadGuiComponent();
			if (loadedGuiComponent != null) {
				cachedElement = new LoadedElement<>(loadedGuiComponent);
			}
		}
		if (cachedElement == null) {
			return failure("not found");
		}
		if (!cachedElement.preconditionValidated && precondition != null && !precondition.test(cachedElement.element)) {
			return failure("precondition failed");
		}

		final String report = reportLine.apply(cachedElement.element); // element may disappear after action
		final Polling<T, U> result = applier.apply(cachedElement.element);
		if (result.isSuccess()) {
			pilot.getActionReport().report(report);
			reportLine = null;
		}
		return result;
	}

	/**
	 * Wait until a component is edited
	 *
	 * @param <U>     return type
	 * @param edition
	 * @param onFail
	 * @return
	 */
	public <U> U waitEdited(final PollingFunction<T, U> edition, final PollingResultFunction<T, U> onFail) {
		return waitActionSuccess(this::canEdit, edition, pilot.getDefaultActionTimeout(), onFail);
	}

	/**
	 * Waits until a component is edited, throwing a java assertion error if edition
	 * failed
	 *
	 * @param <U>     return type
	 * @param edition
	 * @return
	 */
	public <U> U waitEdited(final PollingFunction<T, U> edition) {
		return waitEdited(edition, throwError());
	}

	/**
	 * Waits until a component is in the expected state
	 *
	 * @param <U>   return type
	 * @param check
	 * @return
	 */
	public <U> U waitState(final PollingFunction<T, U> check, final PollingResultFunction<T, U> onFail) {
		return waitActionSuccess(this::canCheck, check, pilot.getDefaultActionTimeout(), onFail);
	}

	/**
	 * Waits until a component is in the expected state, throwing a java assertion
	 * error if the check failed
	 *
	 * @param <U>   return type
	 * @param check
	 * @return
	 */
	public <U> U waitState(final PollingFunction<T, U> check) {
		return waitState(check, throwError());
	}

	/**
	 * To say that the next action will have to wait for some arbitrary delay before
	 * execution
	 *
	 * @param actionDelay
	 * @return
	 */
	public C followedBy(final ActionDelay actionDelay) {
		pilot.setActionDelay(actionDelay);
		return (C) this;
	}

	/**
	 * Wait on the action set by followedByDelay
	 */
	protected void waitActionDelay() {
		final ActionDelay actionDelay = pilot.getActionDelay();
		if (actionDelay != null) {
			pilot.setActionDelay(null);
			actionDelay.waitFinished();
		}
	}

}
