package ch.skymarshall.tcwriter.it;

import ch.skymarshall.tcwriter.annotations.TCApi;

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
		final ParameterValue value = new ParameterValue();
		value.keyValue1 = keyValue;
		return value;
	}

	public String getKeyValue1() {
		return keyValue1;
	}

	public void setKeyValue1(final String keyValue1) {
		this.keyValue1 = keyValue1;
	}

	public String getKeyValue2() {
		return keyValue2;
	}

	public void setKeyValue2(final String keyValue2) {
		this.keyValue2 = keyValue2;
	}

	public String getKeyValue3() {
		return keyValue3;
	}

	public void setKeyValue3(final String keyValue3) {
		this.keyValue3 = keyValue3;
	}

}
