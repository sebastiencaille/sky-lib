package ch.skymarshall.tcwriter.generators.model;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

public class TestMethod {

	private final String id;
	private final List<TestObjectParameter> parameters = new ArrayList<>();

	public TestMethod(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public List<TestObjectParameter> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		return id + ": " + parameters.stream().map(TestObjectParameter::getType).collect(joining(","));
	}

}
