package ch.skymarshall.tcwriter.generators.model;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

public class TestMethod extends IdObject {

	public static TestMethod NO_METHOD = new TestMethod(IdObject.ID_NOT_SET, "");

	private final List<TestObjectParameter> parameters = new ArrayList<>();

	private final String name;

	public TestMethod(final String id, final String name) {
		super(id);
		this.name = name;
	}

	public List<TestObjectParameter> getParameters() {
		return parameters;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name + ": " + parameters.stream().map(TestObjectParameter::getType).collect(joining(","));
	}

}
