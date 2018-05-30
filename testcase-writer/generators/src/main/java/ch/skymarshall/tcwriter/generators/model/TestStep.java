package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.List;

import ch.skymarshall.tcwriter.generators.model.TestParameter.ParameterNature;

public class TestStep {

	private int ordinal;
	private TestActor actor = TestActor.NOT_SET;
	private TestRole role = TestRole.NOT_SET;
	private TestAction action = TestAction.NOT_SET;
	private final List<TestParameterValue> parametersValue = new ArrayList<>();
	private TestParameter reference;

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(final int ordinal) {
		this.ordinal = ordinal;
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

	public TestParameter asNamedReference(final String namedReference) {
		reference = new TestParameter(namedReference, namedReference, ParameterNature.REFERENCE,
				getAction().getReturnType());
		return reference;
	}

	public TestParameter getReference() {
		return reference;
	}

}
