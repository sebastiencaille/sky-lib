package ch.scaille.tcwriter.it.api;

import ch.scaille.tcwriter.annotations.TCApi;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
public class ParameterValue {

	@Nullable
	private String keyValue1 = null;

	@Nullable
	private String keyValue2 = null;

	@Nullable
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
