package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.List;

public class TestObject {

	private final String id;
	private final List<TestObjectParameter> mandatoryParameters = new ArrayList<>();
	private final List<TestObjectParameter> optionalParameters = new ArrayList<>();

	public TestObject(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
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
