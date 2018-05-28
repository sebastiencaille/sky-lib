package ch.skymarshall.tcwriter.generators.model;

import java.util.HashMap;
import java.util.Map;

/** A value in the test case */
public class TestParameterValue extends IdObject {

	public static final TestParameterValue NO_VALUE = new TestParameterValue("", TestParameter.NO_VALUE);

	private final TestParameter testParameter;
	private final Map<String, TestParameterValue> complexTypeValues = new HashMap<>();
	private String simpleValue;

	public TestParameterValue(final String id, final TestParameter testParameter) {
		super(id);
		this.testParameter = testParameter;
	}

	public TestParameter getTestParameter() {
		return testParameter;
	}

	public Map<String, TestParameterValue> getComplexTypeValues() {
		return complexTypeValues;
	}

	public void addComplexTypeValue(final TestParameterValue value) {
		complexTypeValues.put(value.getId(), value);
	}

	public String getSimpleValue() {
		return simpleValue;
	}

	public TestParameterValue setSimpleValue(final String simpleValue) {
		this.simpleValue = simpleValue;
		return this;
	}

	@Override
	public String toString() {
		return "Value " + testParameter.getName() + ":"
				+ (testParameter.isSimpleType() ? getSimpleValue() : getComplexTypeValues());
	}

}
