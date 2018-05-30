package ch.skymarshall.tcwriter.generators.model;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

public class TestAction extends IdObject {

	public static final TestAction NOT_SET = new TestAction(IdObject.ID_NOT_SET, "", "");

	private final List<TestParameterType> parameterTypes = new ArrayList<>();

	private final String name;

	private final String returnType;

	public TestAction(final String id, final String name, final String returnType) {
		super(id);
		this.name = name;
		this.returnType = returnType;
	}

	public List<TestParameterType> getParameters() {
		return parameterTypes;
	}

	public TestParameterType getParameter(final int index) {
		return parameterTypes.get(index);
	}

	public String getName() {
		return name;
	}

	public String getReturnType() {
		return returnType;
	}

	@Override
	public String toString() {
		return name + ": " + parameterTypes.stream().map(TestParameterType::getType).collect(joining(","));
	}

}
