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

public class Timeout {

	public static Timeout createTimeoutFactory(final int hours, final int minutes, final int seconds) {
		return new Timeout(Duration.ofHours(hours).plus(Duration.ofMinutes(minutes)).plus(Duration.ofSeconds(seconds)));
	}

	public static Timeout createTimeoutFactory(final int time, final TimeUnit unit) {
		return new Timeout(Duration.ofMillis(unit.toMillis(time)));
	}

	private final long delay;
	private final Object info;
	private long timeoutTime = -1;

	public Timeout(final Duration duration) {
		this.info = "";
		delay = duration.toMillis();
	}

	private long getTimeout() {
		if (timeoutTime < 0) {
			timeoutTime = System.currentTimeMillis() + delay;
		}
		return timeoutTime;
	}

	public boolean hasTimedOut() {
		return System.currentTimeMillis() > getTimeout();
	}

	public void yield() throws InterruptedException {
		Thread.sleep(Math.min(100, remainingTime()));
	}

	public void waitOn(final Object obj) throws InterruptedException, TimeoutException {
		final long waitTime = remainingTime();
		if (waitTime > 0) {
			obj.wait(waitTime); // NOSONAR
		}
		if (hasTimedOut()) {
			throw new TimeoutException("Time out: " + info);
		}
	}

	public long remainingTime() {
		final long l = getTimeout() - System.currentTimeMillis();
		if (l < 1) {
			return 1;
		}
		return l;
	}

}
