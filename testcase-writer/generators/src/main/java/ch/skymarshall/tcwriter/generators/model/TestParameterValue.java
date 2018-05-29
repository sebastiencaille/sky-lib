package ch.skymarshall.tcwriter.generators.model;

import java.util.HashMap;
import java.util.Map;

/** A value in the test case */
public class TestParameterValue extends IdObject {

	public static final TestParameterValue NO_VALUE = new TestParameterValue("", TestParameter.NO_VALUE);

	private final TestParameter valueDefinition;
	private final Map<String, TestParameterValue> complexTypeValues = new HashMap<>();
	private final String simpleValue;

	public TestParameterValue(final TestParameterType parameterOfValue, final TestParameter valueDefinition) {
		this(parameterOfValue.getId(), valueDefinition, null);
	}

	public TestParameterValue(final TestParameterType parameterOfValue, final TestParameter valueDefinition,
			final String simpleValue) {
		this(parameterOfValue.getId(), valueDefinition, simpleValue);
	}

	public TestParameterValue(final String id, final TestParameter valueDefinition) {
		this(id, valueDefinition, null);
	}

	public TestParameterValue(final String id, final TestParameter valueDefinition, final String simpleValue) {
		super(id);
		this.valueDefinition = valueDefinition;
		this.simpleValue = simpleValue;
		if (valueDefinition.isSimpleType() ^ simpleValue != null) {
			throw new IllegalArgumentException("mismatch between simpleType and valueDefinition");
		}
	}

	public TestParameter getTestParameter() {
		return valueDefinition;
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

	@Override
	public String toString() {
		return "Value " + valueDefinition.getName() + ":"
				+ (valueDefinition.isSimpleType() ? getSimpleValue() : getComplexTypeValues());
	}

}
