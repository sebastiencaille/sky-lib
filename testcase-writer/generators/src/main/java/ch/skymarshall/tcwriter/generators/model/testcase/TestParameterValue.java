package ch.skymarshall.tcwriter.generators.model.testcase;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import ch.skymarshall.tcwriter.generators.model.ExportReference;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory.ParameterNature;

/** A value in the test case */
public class TestParameterValue extends IdObject {

	public static final TestParameterValue NO_VALUE = new TestParameterValue("", TestParameterFactory.NO_FACTORY);

	@JsonIgnore
	private TestParameterFactory factory;

	private final Map<String, TestParameterValue> complexTypeValues = new HashMap<>();
	private final String apiParameterId;
	private String simpleValue;

	protected TestParameterValue() {
		super(null);
		factory = null;
		simpleValue = null;
		apiParameterId = null;
	}

	public TestParameterValue(final TestApiParameter apiParameter, final TestParameterFactory valueFactory) {
		this(UUID.randomUUID().toString(), apiParameter.getId(), valueFactory, null);
	}

	public TestParameterValue(final TestApiParameter apiParameter, final TestParameterFactory valueFactory,
			final String simpleValue) {
		this(apiParameter.getId(), apiParameter.getId(), valueFactory, simpleValue);
	}

	public TestParameterValue(final String apiParameterId, final TestParameterFactory valueFactory,
			final String simpleValue) {
		this(apiParameterId, apiParameterId, valueFactory, simpleValue);
	}

	public TestParameterValue(final String apiParameterId, final TestParameterFactory valueFactory) {
		this(apiParameterId, apiParameterId, valueFactory, null);
	}

	/**
	 *
	 * @param id           either the id of the action's parameter
	 * @param valueFactory
	 * @param simpleValue
	 */
	public TestParameterValue(final String id, final String apiParameterId, final TestParameterFactory valueFactory,
			final String simpleValue) {
		super(id);
		this.apiParameterId = apiParameterId;
		setValueFactory(valueFactory);
		this.simpleValue = simpleValue;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		final TestParameterValue other = (TestParameterValue) obj;
		return Objects.equal(simpleValue, other.simpleValue) && complexTypeValues.equals(other.complexTypeValues)
				&& apiParameterId.equals(other.apiParameterId);
	}

	public String getApiParameterId() {
		return apiParameterId;
	}

	public TestParameterFactory getValueFactory() {
		return factory;
	}

	public void setValueFactory(final TestParameterFactory valueFactory) {
		if (valueFactory == null) {
			throw new InvalidParameterException("Factory must not be null");
		}
		this.factory = valueFactory;
	}

	@JsonProperty
	public ExportReference getTestParameterRef() {
		if (factory.getNature() == ParameterNature.SIMPLE_TYPE) {
			return new ExportReference(factory.getNature().name() + ":" + factory.getType());
		}
		return new ExportReference(factory.getId());
	}

	public void setTestParameterRef(final ExportReference ref) {
		ref.setRestoreAction((tc, id) -> {
			if (id.isEmpty()) {
				setValueFactory(TestParameterFactory.NO_FACTORY);
			} else if (id.startsWith(ParameterNature.SIMPLE_TYPE.name())) {
				setValueFactory(
						TestParameterFactory.simpleType(id.substring(ParameterNature.SIMPLE_TYPE.name().length() + 1)));
			} else {
				setValueFactory((TestParameterFactory) tc.getRestoreValue(id));
			}
		});
	}

	public Map<String, TestParameterValue> getComplexTypeValues() {
		return complexTypeValues;
	}

	public void updateComplexTypeValues(final Map<String, TestParameterValue> idsToValues) {
		for (final Entry<String, TestParameterValue> param : idsToValues.entrySet()) {
			complexTypeValues.get(param.getKey()).setSimpleValue(param.getValue().getSimpleValue());
		}
	}

	public void addComplexTypeValue(final TestParameterValue value) {
		complexTypeValues.put(value.getId(), value);
	}

	public String getSimpleValue() {
		return simpleValue;
	}

	public void setSimpleValue(final String simpleValue) {
		this.simpleValue = simpleValue;
	}

	public TestParameterValue duplicate() {
		final TestParameterValue newValue = new TestParameterValue(getId(), getApiParameterId(), factory, simpleValue);
		for (final Entry<String, TestParameterValue> complexValue : complexTypeValues.entrySet()) {
			newValue.complexTypeValues.put(complexValue.getKey(), complexValue.getValue().duplicate());
		}
		return newValue;
	}

	public TestParameterValue derivate(final TestParameterFactory newFactory) {
		TestParameterFactory newSafeFactory = newFactory;
		if (newSafeFactory == null) {
			newSafeFactory = TestParameterFactory.NO_FACTORY;
		}
		final TestParameterValue newValue = new TestParameterValue(UUID.randomUUID().toString(), getApiParameterId(),
				newSafeFactory, getSimpleValue());
		newValue.getComplexTypeValues().putAll(getComplexTypeValues());
		return newValue;
	}

	public TestParameterValue derivate(final TestApiParameter parameter) {
		final TestParameterValue newValue = new TestParameterValue(parameter, factory, simpleValue);
		newValue.getComplexTypeValues().putAll(getComplexTypeValues());
		return newValue;
	}

	public boolean matches(final TestApiParameter param) {
		return getValueFactory().matches(param);
	}

	@Override
	public String toString() {
		return "Value " + factory.getName() + ":"
				+ (factory.getNature().isSimpleValue() ? getSimpleValue() : getComplexTypeValues());
	}

}
