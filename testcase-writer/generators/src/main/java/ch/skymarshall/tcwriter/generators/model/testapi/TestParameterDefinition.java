package ch.skymarshall.tcwriter.generators.model.testapi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;

/**
 * Definition of a test parameter value
 * 
 * @author scaille
 *
 */
public class TestParameterDefinition extends TestParameterType {

	public enum ParameterNature {
		SIMPLE_TYPE(true), TEST_API_TYPE(false), REFERENCE(true), NOT_SET(false);

		private final boolean requiresSimpleValue;

		private ParameterNature(final boolean requiresSimpleValue) {
			this.requiresSimpleValue = requiresSimpleValue;
		}

		public boolean isSimpleValue() {
			return requiresSimpleValue;
		}
	}

	public static final TestParameterDefinition NO_PARAMETER = new TestParameterDefinition(IdObject.ID_NOT_SET,
			IdObject.ID_NOT_SET, ParameterNature.NOT_SET, "");
	private final List<TestParameterType> mandatoryParameters = new ArrayList<>();
	private final List<TestParameterType> optionalParameters = new ArrayList<>();
	private final ParameterNature nature;

	protected TestParameterDefinition() {
		super();
		this.nature = null;
	}

	public TestParameterDefinition(final String id, final String name, final ParameterNature nature,
			final String type) {
		super(id, name, type);
		this.nature = nature;
	}

	public ParameterNature getNature() {
		return nature;
	}

	public TestParameterType getMandatoryParameter(final int index) {
		return mandatoryParameters.get(index);
	}

	public List<TestParameterType> getMandatoryParameters() {
		return mandatoryParameters;
	}

	public TestParameterType getOptionalParameter(final int index) {
		return optionalParameters.get(index);
	}

	public List<TestParameterType> getOptionalParameters() {
		return optionalParameters;
	}

	public TestParameterValue createTestParameterValue(final Supplier<TestParameterValue> factoryParameterValue,
			final Supplier<String> verbatimValue) {

		switch (getNature()) {
		case SIMPLE_TYPE:
			return new TestParameterValue(getId(), this, verbatimValue.get());
		case REFERENCE:
			return new TestParameterValue(getId(), this, verbatimValue.get());
		case TEST_API_TYPE:
			return factoryParameterValue.get();
		case NOT_SET:
			return null;
		default:
			throw new IllegalStateException("Unhandled: " + this.getNature());
		}
	}

	@Override
	public String toString() {
		return super.toString() + ", " + mandatoryParameters.size() + " mandatory, " + optionalParameters.size()
				+ " optional";
	}

	public static TestParameterDefinition simpleType(final String type) {
		return new TestParameterDefinition("", "", ParameterNature.SIMPLE_TYPE, type);
	}

}
