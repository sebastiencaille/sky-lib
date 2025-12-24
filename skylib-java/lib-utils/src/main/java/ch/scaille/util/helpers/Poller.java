package ch.scaille.util.helpers;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class Poller {

	protected final TimeTracker timeTracker;

	protected final Duration firstDelay;

	protected final DelayFunction delayFunction;

	private int executionCount = 0;

	/*
	 * End of last polling
	 */
	private long lastPolling;

	/**
	 * Creates a poller
	 * 
	 * @param timeout       the poller timeout
	 * @param firstDelay    the delay before the first polling
	 * @param delayFunction the delay between two polls
	 */
	public Poller(Duration timeout, Duration firstDelay, DelayFunction delayFunction) {
		this.timeTracker = new TimeTracker(timeout);
		this.firstDelay = firstDelay;
		this.delayFunction = delayFunction;
	}

	public void sleep(Duration pollingDelay) {
		final var endOfSleep = lastPolling + pollingDelay.toMillis();
		try {
			while (System.currentTimeMillis() < endOfSleep) {
				// Correct time to take the polling's execution 
				final var waitTime = endOfSleep - System.currentTimeMillis();
				if (waitTime > 0) {
					Thread.sleep(waitTime);
				}
			}
			lastPolling = endOfSleep;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Test interrupted");
		}
	}

	public <T> Optional<T> run(Function<Poller, Optional<T>> polling, Predicate<T> isSuccess) {
		beforeRun();
		sleep(firstDelay);
		Optional<T> lastResult;
		do {
			executionCount++;
			lastResult = polling.apply(this);
			if (lastResult.filter(isSuccess).isPresent()) {
				return lastResult;
			}
			if (!hasTimedOut()) {
				sleep(delayFunction.apply(this));
			}
		} while (!hasTimedOut());
		return lastResult;
	}

	protected void beforeRun() {
		if (lastPolling == 0) {
			start();
		}
	}

	public boolean hasTimedOut() {
		// work around time precision
		return timeTracker.hasTimedOut() && timeTracker.overTimeMs() > 30;
	}

	public void start() {
		lastPolling = System.currentTimeMillis();
		timeTracker.start();
	}

	public void executed() {
		executionCount++;
	}

	public int getExecutionCount() {
		return executionCount;
	}

	public TimeTracker getTimeTracker() {
		return timeTracker;
	}

}
