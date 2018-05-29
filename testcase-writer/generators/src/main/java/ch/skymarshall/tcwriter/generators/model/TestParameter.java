package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.List;

public class TestParameter extends TestParameterType {
	public static final String SIMPLE_TYPE = "SimpleType";

	public static final TestParameter NO_VALUE = new TestParameter(IdObject.ID_NOT_SET, "", "");
	private final List<TestParameterType> mandatoryParameters = new ArrayList<>();
	private final List<TestParameterType> optionalParameters = new ArrayList<>();

	public TestParameter(final String id, final String name, final String type) {
		super(id, name, type);
	}

	public boolean isSimpleType() {
		return SIMPLE_TYPE.equals(getId());
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
		return new TestParameter(SIMPLE_TYPE, SIMPLE_TYPE, type);
	}
}
