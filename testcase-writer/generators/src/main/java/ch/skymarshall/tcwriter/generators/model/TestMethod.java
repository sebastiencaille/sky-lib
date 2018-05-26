package ch.skymarshall.tcwriter.generators.model;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

public class TestMethod extends IdObject {

	public static TestMethod NO_METHOD = new TestMethod(IdObject.ID_NOT_SET);

	private final List<TestObjectParameter> parameters = new ArrayList<>();

	public TestMethod(final String id) {
		super(id);
	}

	public List<TestObjectParameter> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		return getId() + ": " + parameters.stream().map(TestObjectParameter::getType).collect(joining(","));
	}

}
