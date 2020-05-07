package ch.skymarshall.tcwriter.pilot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Assert;

public abstract class AbstractGuiAction<T> {

	protected class LoadedElement {
		public final T element;
		private boolean preconditionValidated;

		public LoadedElement(final T element) {
			super();
			this.element = element;
		}

		public boolean isPreconditionValidated() {
			return preconditionValidated;
		}

		public void setPreconditionValidated() {
			this.preconditionValidated = true;
		}

	}

	protected abstract T loadElement();

	private final GuiPilot pilot;

	protected LoadedElement cachedElement = null;
	protected List<Consumer<T>> postExecutions = new ArrayList<>();
	protected boolean fired = false;

	public AbstractGuiAction(final GuiPilot pilot) {
		this.pilot = pilot;
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

	public void addPostExecution(final Consumer<T> postExec) {
		postExecutions.add(postExec);
		if (fired) {
			postExec.accept(cachedElement.element);
		}
	}

	/**
	 * Executes until condition is true
	 *
	 * @param condition
	 * @param applier
	 * @param timeout
	 * @return
	 */
	public <U> U executeOnCondition(final Predicate<T> precondition, final Function<T, Optional<U>> applier,
			final Duration timeout) {

		waitActionDelay();

		final Optional<U> result = waitActionProcessed(precondition, applier, timeout);
		if (result.isPresent()) {
			fired = true;
			postExecutions.stream().forEach(p -> p.accept(cachedElement.element));
			return result.get();
		}

		Assert.fail("Execution failed: " + toString());
		return null;
	}

	protected <U> Optional<U> waitActionProcessed(final Predicate<T> precondition,
			final Function<T, Optional<U>> applier, final Duration timeout) {
		final long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < timeout.toMillis()) {
			final Optional<U> result = executeOnConditionUnsafe(precondition, applier);
			if (result.isPresent()) {
				return result;
			}
		}
		return Optional.empty();
	}

	/**
	 * Try to execute on condition
	 *
	 * @param precondition
	 * @param applier
	 * @return
	 */
	protected <U> Optional<U> executeOnConditionUnsafe(final Predicate<T> precondition,
			final Function<T, Optional<U>> applier) {
		if (cachedElement == null) {
			final T loadedElement = loadElement();
			if (loadedElement != null) {
				cachedElement = new LoadedElement(loadedElement);
			}
		}
		if (cachedElement == null) {
			return Optional.empty();
		}
		if (!cachedElement.preconditionValidated && precondition != null && !precondition.test(cachedElement.element)) {
			return Optional.empty();
		}
		return applier.apply(cachedElement.element);
	}

	/**
	 * To say that the next action will have to wait for some arbitrary delay before
	 * execution
	 *
	 * @param actionDelay
	 * @return
	 */
	public AbstractGuiAction<T> followedByDelay(final ActionDelay actionDelay) {
		pilot.setActionDelay(actionDelay);
		return this;
	}

	protected void waitActionDelay() {
		final ActionDelay actionDelay = pilot.getActionDelay();
		if (actionDelay != null) {
			pilot.setActionDelay(null);
			actionDelay.waitFinished();
		}
	}

}
