package ch.scaille.tcwriter.model.testcase;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import ch.scaille.tcwriter.mappers.Default;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Objects;

import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.dictionary.TestApiParameter;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory;
import lombok.Getter;
import lombok.Setter;

/**
 * A value in the test case
 */
@Getter
@Setter
public class TestParameterValue extends IdObject {

    public static final TestParameterValue NO_VALUE = new TestParameterValue(IdObject.ID_NOT_SET, "", TestParameterFactory.NO_FACTORY, "");
    private final String apiParameterId;

    /**
     * A test api used to create selector/parameters, or a reference, or a simple
     * value type
     */
    protected TestParameterFactory parameterValueFactory;

    private final Map<String, TestParameterValue> complexTypeValues = new HashMap<>();

    private String simpleValue;

    /**
     * @param id either the id of the action's parameter
     */
    public TestParameterValue(final String id, final String apiParameterId, final TestParameterFactory parameterValueFactory,
                              final String simpleValue) {
        super(id);
        this.apiParameterId = apiParameterId;
        this.setParameterValueFactory(parameterValueFactory);
        this.simpleValue = simpleValue;
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

    @Default
    @JsonCreator
    public TestParameterValue(final String id, final String apiParameterId,
                              Map<String, TestParameterValue> complexTypeValues, String simpleValue) {
        super(id);
        this.apiParameterId = apiParameterId;
        this.complexTypeValues.putAll(complexTypeValues);
        this.simpleValue = simpleValue;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final var other = (TestParameterValue) obj;
        return Objects.equal(simpleValue, other.simpleValue) && complexTypeValues.equals(other.complexTypeValues)
                && apiParameterId.equals(other.apiParameterId);
    }

    @Override
    public int hashCode() {
        return simpleValue.hashCode() + complexTypeValues.hashCode() * 13 + apiParameterId.hashCode() * 29;
    }

    public void setParameterValueFactory(final TestParameterFactory valueFactory) {
        this.parameterValueFactory = java.util.Objects.requireNonNull(valueFactory,"Factory must not be null");
    }

    public void updateComplexTypeValues(final Map<String, TestParameterValue> idsToValues) {
        for (final var testParam : idsToValues.entrySet()) {
            complexTypeValues.get(testParam.getKey()).setSimpleValue(testParam.getValue().getSimpleValue());
        }
    }

    public void addComplexTypeValue(final TestParameterValue value) {
        complexTypeValues.put(value.getId(), value);
    }

    public TestParameterValue duplicate() {
        final var newParamValue = createTestParameterValue(getId(), getApiParameterId(), parameterValueFactory, simpleValue);
        for (final Entry<String, TestParameterValue> complexValue : complexTypeValues.entrySet()) {
            newParamValue.complexTypeValues.put(complexValue.getKey(), complexValue.getValue().duplicate());
        }
        return newParamValue;
    }

    public TestParameterValue derivate(final TestParameterFactory newFactory) {
        var newFactorySafe = newFactory;
        if (newFactorySafe == null) {
            newFactorySafe = TestParameterFactory.NO_FACTORY;
        }
        final var newValue = createTestParameterValue(UUID.randomUUID().toString(), getApiParameterId(),
                newFactorySafe, getSimpleValue());
        newValue.getComplexTypeValues().putAll(getComplexTypeValues());
        return newValue;
    }

    public TestParameterValue derivate(final TestApiParameter parameter) {
        final var newValue = createTestParameterValue(UUID.randomUUID().toString(), parameter.getId(), parameterValueFactory, simpleValue);
        newValue.getComplexTypeValues().putAll(getComplexTypeValues());
        return newValue;
    }

    protected TestParameterValue createTestParameterValue(final String id, final String apiParameterId, final TestParameterFactory valueFactory,
                                                          final String simpleValue) {
        return new TestParameterValue(id, apiParameterId, valueFactory, simpleValue);
    }

    public boolean matches(final TestApiParameter param) {
        return getParameterValueFactory().matches(param);
    }

    @Override
    public String toString() {
        return "Value " + parameterValueFactory.getName() + ":"
                + (parameterValueFactory.getNature().isSimpleValue() ? getSimpleValue() : getComplexTypeValues());
    }

}
