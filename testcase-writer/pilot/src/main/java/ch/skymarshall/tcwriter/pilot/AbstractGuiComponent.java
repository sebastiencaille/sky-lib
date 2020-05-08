package ch.skymarshall.tcwriter.pilot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.junit.Assert;

public abstract class AbstractGuiComponent<T> {

	protected class LoadedElement {
		public final T element;
		private boolean preconditionValidated;

		public LoadedElement(final T element) {
			this.element = element;
		}

		public boolean isPreconditionValidated() {
			return preconditionValidated;
		}

		public void setPreconditionValidated() {
			this.preconditionValidated = true;
		}

	}

	protected class PollingResult<U> {
		public final U value;
		public final Throwable failureReason;
		private LoadedElement loadedElement;

		public PollingResult(final U value, final Throwable failureReason) {
			this.value = value;
			this.failureReason = failureReason;
		}

		public T getFoundElement() {
			if (loadedElement != null) {
				return loadedElement.element;
			}
			return null;
		}

		public void setLoadedElement(final AbstractGuiComponent<T>.LoadedElement loadedElement) {
			this.loadedElement = loadedElement;
		}

		public boolean success() {
			return failureReason == null;
		}

		public U orElse(final U orElse) {
			if (success()) {
				return value;
			}
			return orElse;
		}

		public U orElseGet(final Supplier<U> orElse) {
			if (success()) {
				return value;
			}
			return orElse.get();
		}

	}

	protected <U> PollingResult<U> value(final U value) {
		return new PollingResult<>(value, null);
	}

	protected <U> PollingResult<U> failure(final String reason) {
		return new PollingResult<>(null, new RuntimeException(reason));
	}

	protected <U> PollingResult<U> onException(final Throwable cause) {
		return new PollingResult<>(null, cause);
	}

	protected Function<T, PollingResult<Boolean>> action(final Consumer<T> consumer) {
		return t -> {
			consumer.accept(t);
			return value(Boolean.TRUE);
		};
	}

	protected abstract T loadElement();

	private final GuiPilot pilot;
	private final List<Consumer<T>> postExecutions = new ArrayList<>();

	private LoadedElement cachedElement = null;
	protected boolean fired = false;
	private Function<T, String> reportLine = t -> null;

	public AbstractGuiComponent(final GuiPilot pilot) {
		this.pilot = pilot;
	}

	/**
	 * Fails using some text
	 *
	 * @param actionDescr
	 * @return
	 */
	protected <U> Function<PollingResult<U>, U> assertFail(final String actionDescr) {
		return r -> {
			Assert.fail("Action failed [" + actionDescr + "]: " + r.failureReason);
			return null;
		};
	}

	/**
	 * Fails using reporting line. The line must handle null T if it was not found
	 *
	 * @param actionDescr
	 * @return
	 */
	protected <U> Function<PollingResult<U>, U> assertFail() {
		return r -> {
			Assert.fail("Action failed: [" + reportLine.apply(r.getFoundElement()) + "]: " + r.failureReason);
			return null;
		};
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
	public AbstractGuiComponent<T> addPostExecution(final Consumer<T> postExec) {
		postExecutions.add(postExec);
		if (fired) {
			postExec.accept(cachedElement.element);
		}
		return this;
	}

	public AbstractGuiComponent<T> addReporting(final Function<T, String> reportLine) {
		this.reportLine = reportLine;
		return this;
	}

	/**
	 * Executes until condition is true. This method wais for the delayActions and
	 * fires the post executions. Use this to method to protect execution of
	 * actions.
	 *
	 * Prefer overriding waitActionSuccessLoop
	 *
	 * @param condition
	 * @param applier
	 * @param timeout
	 * @return
	 */
	protected <U> U waitActionSuccess(final Predicate<T> precondition, final Function<T, PollingResult<U>> applier,
			final Duration timeout, final Function<PollingResult<U>, U> onFail) {

		waitActionDelay();

		final PollingResult<U> result = waitActionSuccessLoop(precondition, applier, timeout);
		if (result.success()) {
			fired = true;
			postExecutions.stream().forEach(p -> p.accept(cachedElement.element));
		}

		result.setLoadedElement(cachedElement);
		return result.orElseGet(() -> onFail.apply(result));
	}

	/**
	 * Loops until the action is processed. Can be overwritten by custom code
	 *
	 * @param <U>          type of returned value (component, text, ...)
	 * @param precondition a precondition
	 * @param applier      action applied on component
	 * @param reporting    reporting, if action is successful
	 * @param timeout
	 * @return an optional on a response
	 */
	protected <U> PollingResult<U> waitActionSuccessLoop(final Predicate<T> precondition,
			final Function<T, PollingResult<U>> applier, final Duration timeout) {
		final long startTime = System.currentTimeMillis();
		PollingResult<U> lastResult = failure("No information");
		while (System.currentTimeMillis() - startTime < timeout.toMillis()) {
			lastResult = executePolling(precondition, applier);
			if (lastResult.success()) {
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
	 * Try to execute the action
	 *
	 * @param precondition
	 * @param applier
	 * @return
	 */
	protected <U> PollingResult<U> executePolling(final Predicate<T> precondition,
			final Function<T, PollingResult<U>> applier) {
		if (cachedElement == null) {
			final T loadedElement = loadElement();
			if (loadedElement != null) {
				cachedElement = new LoadedElement(loadedElement);
			}
		}
		if (cachedElement == null) {
			return failure("not found");
		}
		if (!cachedElement.preconditionValidated && precondition != null && !precondition.test(cachedElement.element)) {
			return failure("precondition failed");
		}

		final String report = reportLine.apply(cachedElement.element); // element may disappear after action
		final PollingResult<U> result = applier.apply(cachedElement.element);
		if (result.success()) {
			pilot.getActionReport().report(report);
			reportLine = null;
		}
		return result;
	}

	/**
	 * To say that the next action will have to wait for some arbitrary delay before
	 * execution
	 *
	 * @param actionDelay
	 * @return
	 */
	public AbstractGuiComponent<T> followedByDelay(final ActionDelay actionDelay) {
		pilot.setActionDelay(actionDelay);
		return this;
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
