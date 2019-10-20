package ch.skymarshall.tcwriter.generators.model.testcase;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.skymarshall.tcwriter.generators.model.ExportReference;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterDefinition;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterDefinition.ParameterNature;

/** A value in the test case */
public class TestParameterValue extends IdObject {

	public static final TestParameterValue NO_VALUE = new TestParameterValue("", TestParameterDefinition.NO_PARAMETER);

	@JsonIgnore
	private TestParameterDefinition valueDefinition;

	private final Map<String, TestParameterValue> complexTypeValues = new HashMap<>();
	private final String simpleValue;
	private final String apiParameterId;

	protected TestParameterValue() {
		super(null);
		valueDefinition = null;
		simpleValue = null;
		apiParameterId = null;
	}

	public TestParameterValue(final TestApiParameter apiParameter, final TestParameterDefinition valueDefinition) {
		this(UUID.randomUUID().toString(), apiParameter.getId(), valueDefinition, null);
	}

	public TestParameterValue(final TestApiParameter apiParameter, final TestParameterDefinition valueDefinition,
			final String simpleValue) {
		this(UUID.randomUUID().toString(), apiParameter.getId(), valueDefinition, simpleValue);
	}

	public TestParameterValue(final String apiParameter, final TestParameterDefinition valueDefinition) {
		this(UUID.randomUUID().toString(), apiParameter, valueDefinition, null);
	}

	/**
	 *
	 * @param id              either the id of the action's parameter
	 * @param valueDefinition
	 * @param simpleValue
	 */
	public TestParameterValue(final String id, final String apiParameterId,
			final TestParameterDefinition valueDefinition, final String simpleValue) {
		super(id);
		this.apiParameterId = apiParameterId;
		this.valueDefinition = valueDefinition;
		this.simpleValue = simpleValue;
	}

	public String getApiParameterId() {
		return apiParameterId;
	}

	public TestParameterDefinition getValueDefinition() {
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
				valueDefinition = TestParameterDefinition
						.simpleType(id.substring(ParameterNature.SIMPLE_TYPE.name().length() + 1));
			} else {
				valueDefinition = (TestParameterDefinition) tc.getRestoreValue(id);
			}
		});
	}

	public Map<String, TestParameterValue> getComplexTypeValues() {
		return complexTypeValues;
	}

	public void addComplexTypeValue(final TestParameterValue value) {
		complexTypeValues.put(value.getApiParameterId(), value);
	}

	public String getSimpleValue() {
		return simpleValue;
	}

	public TestParameterValue duplicate() {
		final TestParameterValue newValue = new TestParameterValue(getId(), getApiParameterId(), valueDefinition,
				simpleValue);
		for (final Entry<String, TestParameterValue> complexValue : complexTypeValues.entrySet()) {
			newValue.complexTypeValues.put(complexValue.getKey(), complexValue.getValue().duplicate());
		}
		return newValue;
	}

	@Override
	public String toString() {
		return "Value " + valueDefinition.getName() + ":"
				+ (valueDefinition.getNature().isSimpleValue() ? getSimpleValue() : getComplexTypeValues());
	}

}
