package org.skymarshall.hmi;

import java.text.DecimalFormat;

public class Utils {

	public static String toSize(final Number size) {
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
