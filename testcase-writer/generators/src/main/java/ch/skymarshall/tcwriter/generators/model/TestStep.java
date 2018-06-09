package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.List;

public class TestStep {

	private final int ordinal;
	private TestActor actor = TestActor.NOT_SET;
	private TestRole role = TestRole.NOT_SET;
	private TestAction action = TestAction.NOT_SET;
	private final List<TestParameterValue> parametersValue = new ArrayList<>();
	private TestReference reference;

	public TestStep(final int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public TestActor getActor() {
		return actor;
	}

	public void setActor(final TestActor actor) {
		this.actor = actor;
		this.role = actor.getRole();
	}

	public TestRole getRole() {
		return role;
	}

	public TestAction getAction() {
		return action;
	}

	public void setAction(final TestAction action) {
		this.action = action;
	}

	public List<TestParameterValue> getParametersValue() {
		return parametersValue;
	}

	public void addParameter(final TestParameterValue parameterValue) {
		this.parametersValue.add(parameterValue);
	}

	public TestReference asNamedReference(final String namedReference, final String description) {
		reference = new TestReference(this, namedReference, getAction().getReturnType(), description);
		return reference;
	}

	public TestReference getReference() {
		return reference;
	}

}
