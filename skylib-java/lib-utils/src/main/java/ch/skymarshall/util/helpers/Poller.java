package ch.skymarshall.util.helpers;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Predicate;

public class Poller {

	public interface DelayFunction extends Function<Poller, Duration> {
		// inherited
	}

	public final TimeTracker timeTracker;

	protected final Duration firstDelay;

	protected final DelayFunction delayFunction;

	private int executionCount = 0;

	private long lastPolling;
	
	public Poller(Duration timeout, Duration firstDelay, DelayFunction delayFunction) {
		this.timeTracker = new TimeTracker(timeout);
		this.firstDelay = firstDelay;
		this.delayFunction = delayFunction;
	}

	public void sleep() {
		sleep(delayFunction.apply(this));
	}

	public void sleep(Duration pollingDelay) {
		try {
			// Correct time
			long waitTime = pollingDelay.toMillis() - (System.currentTimeMillis() - lastPolling);
			if (waitTime <= 10) {
				return;
			}
			Thread.sleep(waitTime);
			lastPolling = lastPolling + waitTime;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Test interrupted");
		}
	}

	public <T> T run(Function<Poller, T> polling, Predicate<T> isSuccess) {
		beforeRun();
		sleep(firstDelay);
		T result;
		do {
			executionCount++;
			result = polling.apply(this);
			if (isSuccess.test(result)) {
				return result;
			}
			sleep(delayFunction.apply(this));
		} while (!hasTimedOut());
		return result;
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
