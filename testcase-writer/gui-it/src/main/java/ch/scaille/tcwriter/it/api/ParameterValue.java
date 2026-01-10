package ch.scaille.tcwriter.it.api;

import ch.scaille.tcwriter.annotations.TCApi;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ParameterValue {

	private String keyValue1 = null;

	private String keyValue2 = null;

	private String keyValue3 = null;

	public static ParameterValue value() {
		return new ParameterValue();
	}

	@TCApi(description = "One key:value", humanReadable = "Key:Value")
	public static ParameterValue oneValue(
			@TCApi(description = "Key:Value 1", humanReadable = "Key:Value 1") final String keyValue) {
		final var value = new ParameterValue();
		value.keyValue1 = keyValue;
		return value;
	}

}
