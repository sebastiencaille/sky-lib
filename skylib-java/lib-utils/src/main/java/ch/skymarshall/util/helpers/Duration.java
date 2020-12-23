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

public class Duration {

	private final long millisecondsInDay;
	private final int days;
	private final int months;
	private final int years;

	public Duration(final int hours, final int minutes, final int seconds) {
		millisecondsInDay = TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes)
				+ TimeUnit.SECONDS.toMillis(seconds);
		days = 0;
		months = 0;
		years = 0;
	}

	public Duration(final int time, final TimeUnit unit) {
		millisecondsInDay = unit.toMillis(time);
		days = 0;
		months = 0;
		years = 0;
	}

	public Calendar addTo(final Calendar cal) {
		final Calendar clone = (Calendar) cal.clone();
		clone.add(Calendar.MILLISECOND, (int) millisecondsInDay);
		clone.add(Calendar.DAY_OF_YEAR, days);
		clone.add(Calendar.MONTH, months);
		clone.add(Calendar.YEAR, years);
		return clone;
	}

}
