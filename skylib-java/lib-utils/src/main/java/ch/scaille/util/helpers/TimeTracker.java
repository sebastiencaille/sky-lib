package ch.scaille.util.helpers;

import lombok.Getter;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class TimeTracker {

	public static TimeTracker createTimeoutFactory(final int hours, final int minutes, final int seconds) {
		return new TimeTracker(
				Duration.ofHours(hours).plus(Duration.ofMinutes(minutes)).plus(Duration.ofSeconds(seconds)));
	}

	public static TimeTracker createTimeoutFactory(final int time, final TimeUnit unit) {
		return new TimeTracker(Duration.ofMillis(unit.toMillis(time)));
	}

	@Getter
    private final Duration duration;
	private long absoluteTimeout = -1;

	public TimeTracker(final Duration duration) {
		this.duration = duration;
	}

    public void start() {
		getAbsoluteTimeout();
	}

	private long getAbsoluteTimeout() {
		if (absoluteTimeout < 0) {
			absoluteTimeout = System.currentTimeMillis() + duration.toMillis();
		}
		return absoluteTimeout;
	}

	public boolean hasTimedOut() {
		return System.currentTimeMillis() > getAbsoluteTimeout();
	}

	public long elapsedTimeMs() {
		return duration.toMillis() - remainingTimeMs();
	}

	public Duration remainingDuration() {
		// TODO must be shorter for the 1st run
		return Duration.ofMillis(remainingTimeMs());
	}

	public long remainingTimeMs() {
		final var l = getAbsoluteTimeout() - System.currentTimeMillis();
		if (l < 1) {
			return 1;
		}
		return l;
	}

	public long overTimeMs() {
		return Math.max(0, System.currentTimeMillis() - getAbsoluteTimeout());
	}
}
