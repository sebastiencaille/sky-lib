/*******************************************************************************
 * Copyright (c) 2013 Sebastien Caille.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.util.helpers;

import java.util.Calendar;

public class Duration {

    private final int millisecondsInDay;
    private final int days;
    private final int months;
    private final int years;

    public Duration(final int hours, final int minutes, final int seconds) {
        millisecondsInDay = (hours * 3600 + minutes * 60 + seconds) * 1000;
        days = 0;
        months = 0;
        years = 0;
    }

    public Calendar addTo(final Calendar cal) {
        final Calendar clone = (Calendar) cal.clone();
        clone.add(Calendar.MILLISECOND, millisecondsInDay);
        clone.add(Calendar.DAY_OF_YEAR, days);
        clone.add(Calendar.MONTH, months);
        clone.add(Calendar.YEAR, years);
        return clone;
    }

}
