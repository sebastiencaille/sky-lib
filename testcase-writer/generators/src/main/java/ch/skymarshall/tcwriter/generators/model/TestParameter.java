package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.List;

public class TestParameter extends TestParameterType {

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

	public static final TestParameter NO_VALUE = new TestParameter(IdObject.ID_NOT_SET, IdObject.ID_NOT_SET,
			ParameterNature.NOT_SET, "");
	private final List<TestParameterType> mandatoryParameters = new ArrayList<>();
	private final List<TestParameterType> optionalParameters = new ArrayList<>();
	private final ParameterNature nature;

	public TestParameter(final String id, final String name, final ParameterNature nature, final String type) {
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

	@Override
	public String toString() {
		return super.toString() + ", " + mandatoryParameters.size() + " mandatory, " + optionalParameters.size()
				+ " optional";
	}

	public static TestParameter simpleType(final String type) {
		return new TestParameter("", "", ParameterNature.SIMPLE_TYPE, type);
	}

}
