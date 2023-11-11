package ch.scaille.util.text;

import java.text.DecimalFormat;

public interface FormatterHelper {

	static String toSize(final Number size) {
		var val = size.floatValue();

		var unit = "";
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
