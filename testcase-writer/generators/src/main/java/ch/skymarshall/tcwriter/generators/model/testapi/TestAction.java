package ch.skymarshall.tcwriter.generators.model.testapi;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.NamedObject;

public class TestAction extends NamedObject {

	public static final TestAction NOT_SET = new TestAction(IdObject.ID_NOT_SET, "", "");

	private final List<TestApiParameter> parameterTypes = new ArrayList<>();

	private final String returnType;

	protected TestAction() {
		super(null, null);
		returnType = null;
	}

	public TestAction(final String id, final String name, final String returnType) {
		super(id, name);
		this.returnType = returnType;
	}

	public List<TestApiParameter> getParameters() {
		return parameterTypes;
	}

	public TestApiParameter getParameter(final int index) {
		return parameterTypes.get(index);
	}

	public String getReturnType() {
		return returnType;
	}

	@Override
	public String toString() {
		return getName() + ": " + parameterTypes.stream().map(TestApiParameter::getType).collect(joining(","));
	}

}
