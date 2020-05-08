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

	protected static <T> Function<T, Optional<Boolean>> consumer(final Consumer<T> consumer) {
		return t -> {
			consumer.accept(t);
			return Optional.of(Boolean.TRUE);
		};
	}

	protected abstract T loadElement();

	private final GuiPilot pilot;
	private final List<Consumer<T>> postExecutions = new ArrayList<>();

	protected LoadedElement cachedElement = null;
	protected boolean fired = false;
	private Function<T, String> reportLine = (t) -> null;

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

	/**
	 * Adds a post-action, which is executed once action is finished
	 *
	 * @param postExec
	 */
	public AbstractGuiAction<T> addPostExecution(final Consumer<T> postExec) {
		postExecutions.add(postExec);
		if (fired) {
			postExec.accept(cachedElement.element);
		}
		return this;
	}

	public AbstractGuiAction<T> addReporting(final Function<T, String> reportLine) {
		this.reportLine = reportLine;
		return this;
	}

	/**
	 * Executes until condition is true
	 *
	 * @param condition
	 * @param applier
	 * @param timeout
	 * @return
	 */
	protected <U> U waitActionSuccess(final Predicate<T> precondition, final Function<T, Optional<U>> applier,
			final Duration timeout) {

		waitActionDelay();

		final Optional<U> result = waitActionSuccessLoop(precondition, applier, timeout);
		if (result.isPresent()) {
			fired = true;
			postExecutions.stream().forEach(p -> p.accept(cachedElement.element));
			return result.get();
		}

		Assert.fail("Execution failed: " + toString());
		return null;
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
	protected <U> Optional<U> waitActionSuccessLoop(final Predicate<T> precondition,
			final Function<T, Optional<U>> applier, final Duration timeout) {
		final long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < timeout.toMillis()) {
			final Optional<U> result = executeActionOnce(precondition, applier);
			if (result.isPresent()) {
				return result;
			}
		}
		return Optional.empty();
	}

	/**
	 * Try to execute the action
	 *
	 * @param precondition
	 * @param applier
	 * @return
	 */
	protected <U> Optional<U> executeActionOnce(final Predicate<T> precondition,
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
		final String report = reportLine.apply(cachedElement.element);
		final Optional<U> result = applier.apply(cachedElement.element);
		if (result.isPresent()) {
			pilot.getActionReport().report(report);
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
