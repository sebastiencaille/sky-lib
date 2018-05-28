package ch.skymarshall.tcwriter.generators.model;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

public class TestAction extends IdObject {

	public static TestAction NO_METHOD = new TestAction(IdObject.ID_NOT_SET, "");

	private final List<TestParameterType> parameterTypes = new ArrayList<>();

	private final String name;

	public TestAction(final String id, final String name) {
		super(id);
		this.name = name;
	}

	public List<TestParameterType> getParameters() {
		return parameterTypes;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name + ": " + parameterTypes.stream().map(TestParameterType::getType).collect(joining(","));
	}

}
