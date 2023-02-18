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
package ch.scaille.util;

import java.text.DecimalFormat;

public interface FormatterHelper {

	static String toSize(final Number size) {
		float val = size.floatValue();

		String unit = "";
		if (val > 1024) {
			unit = "Ko";
			val = val / 1024;
		}
		if (val > 1024) {
			unit = "Mo";
			val = val / 1024;
		}
		if (val > 1024) {
			unit = "Go";
			val = val / 1024;
		}
		return new DecimalFormat("##0.0").format(val) + unit;

	}

}
