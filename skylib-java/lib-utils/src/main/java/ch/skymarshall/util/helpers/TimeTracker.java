/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.util.helpers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TimeTracker {

	public static TimeTracker createTimeoutFactory(final int hours, final int minutes, final int seconds) {
		return new TimeTracker(Duration.ofHours(hours).plus(Duration.ofMinutes(minutes)).plus(Duration.ofSeconds(seconds)));
	}

	public static TimeTracker createTimeoutFactory(final int time, final TimeUnit unit) {
		return new TimeTracker(Duration.ofMillis(unit.toMillis(time)));
	}

	private final Duration duration;
	private long absoluteTimeout = -1;
	private final Object info;

	public TimeTracker(final Duration duration) {
		this.info = "";
		this.duration = duration;
	}

	public Duration getDuration() {
		return duration;
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

	public void waitOn(final Object obj) throws InterruptedException, TimeoutException {
		final long waitTime = remainingTimeMs();
		if (waitTime > 0) {
			obj.wait(waitTime); // NOSONAR
		}
		if (hasTimedOut()) {
			throw new TimeoutException("Time out: " + info);
		}
	}

	public long elapsedTimeMs() {
		return duration.toMillis() - remainingTimeMs();
	}

	public Duration remainingDuration() {
		return Duration.ofMillis(remainingTimeMs());
	}

	public long remainingTimeMs() {
		final long l = getAbsoluteTimeout() - System.currentTimeMillis();
		if (l < 1) {
			return 1;
		}
		return l;
	}
	
	public long overTimeMs() {
		return Math.max(0, System.currentTimeMillis() - getAbsoluteTimeout());
	}
}
