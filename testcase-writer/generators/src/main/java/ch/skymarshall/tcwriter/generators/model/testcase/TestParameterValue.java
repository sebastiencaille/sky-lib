package ch.skymarshall.tcwriter.generators.model.testcase;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.skymarshall.tcwriter.generators.model.ExportReference;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameter.ParameterNature;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterType;

/** A value in the test case */
public class TestParameterValue extends IdObject {

	public static final TestParameterValue NO_VALUE = new TestParameterValue("", TestParameter.NO_VALUE);

	@JsonIgnore
	private TestParameter valueDefinition;

	private final Map<String, TestParameterValue> complexTypeValues = new HashMap<>();
	private final String simpleValue;

	protected TestParameterValue() {
		super(null);
		valueDefinition = null;
		simpleValue = null;
	}

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
		if (valueDefinition.getNature().isSimpleValue() ^ simpleValue != null) {
			throw new IllegalArgumentException("mismatch between simpleType and nature");
		}
	}

	public TestParameter getValueDefinition() {
		return valueDefinition;
	}

	@JsonProperty
	public ExportReference getTestParameterRef() {
		if (valueDefinition.getNature() == ParameterNature.SIMPLE_TYPE) {
			return new ExportReference(valueDefinition.getNature().name() + ":" + valueDefinition.getType());
		}
		return new ExportReference(valueDefinition.getId());
	}

	public void setTestParameterRef(final ExportReference ref) {
		ref.setRestoreAction((tc, id) -> {
			if (id.startsWith(ParameterNature.SIMPLE_TYPE.name())) {
				valueDefinition = TestParameter
						.simpleType(id.substring(ParameterNature.SIMPLE_TYPE.name().length() + 1));
			} else {
				valueDefinition = (TestParameter) tc.getRestoreValue(id);
			}
		});
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
				+ (valueDefinition.getNature().isSimpleValue() ? getSimpleValue() : getComplexTypeValues());
	}

}
