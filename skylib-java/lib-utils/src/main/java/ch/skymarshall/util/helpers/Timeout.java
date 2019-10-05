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

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Timeout {

	public static class TimeoutFactory extends Duration {

		public TimeoutFactory(final int hours, final int minutes, final int seconds) {
			super(hours, minutes, seconds);
		}

		public TimeoutFactory(final int time, final TimeUnit unit) {
			super(time, unit);
		}

		public Timeout createTimeout() {
			return new Timeout(addTo(Calendar.getInstance()), this);
		}
	}

	private final long stop;
	private final Object info;

	public Timeout(final Calendar cal, final Object info) {
		this.info = info;
		stop = cal.getTimeInMillis();
	}

	public boolean hasTimedOut() {
		return System.currentTimeMillis() > stop;
	}

	public void yield() throws TimeoutException {
		if (hasTimedOut()) {
			throw new TimeoutException("Time out: " + info);
		}
		try {
			Thread.sleep(100);
		} catch (final InterruptedException e) { // NOSONAR
			throw new TimeoutException("Time out (interrupted): " + info);
		}
	}

	public TimeoutFactory createTimeoutFactory(final int hours, final int minutes, final int seconds) {
		return new TimeoutFactory(hours, minutes, seconds);
	}

	public void waitOn(final Object obj) throws InterruptedException, TimeoutException {
		final long waitTime = stop - System.currentTimeMillis();
		if (waitTime > 0) {
			obj.wait(waitTime); // NOSONAR
		}
		if (hasTimedOut()) {
			throw new TimeoutException("Time out: " + info);
		}

	}

	public long remaining() {
		final long l = stop - System.currentTimeMillis();
		if (l < 1) {
			return 1;
		}
		return l;
	}

}
