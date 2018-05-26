package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.List;

public class TestObject extends IdObject {

	public static final TestObject NO_VALUE = new TestObject(IdObject.ID_NOT_SET, "");
	private final List<TestObjectParameter> mandatoryParameters = new ArrayList<>();
	private final List<TestObjectParameter> optionalParameters = new ArrayList<>();
	private final String type;

	public TestObject(final String id, final String type) {
		super(id);
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public List<TestObjectParameter> getMandatoryParameters() {
		return mandatoryParameters;
	}

	public List<TestObjectParameter> getOptionalParameters() {
		return optionalParameters;
	}

	@Override
	public String toString() {
		return "Test object: " + mandatoryParameters.size() + " mandatory, " + optionalParameters.size() + " optional";
	}
}
