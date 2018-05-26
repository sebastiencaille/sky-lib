package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.List;

public class TestObject extends IdObject {

	private final List<TestObjectParameter> mandatoryParameters = new ArrayList<>();
	private final List<TestObjectParameter> optionalParameters = new ArrayList<>();

	public TestObject(final String id) {
		super(id);
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
