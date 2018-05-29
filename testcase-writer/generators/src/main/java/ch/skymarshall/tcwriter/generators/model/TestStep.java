package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.List;

import ch.skymarshall.tcwriter.generators.model.TestParameter.ParameterNature;

public class TestStep {

	private int ordinal;
	private TestActor actor;
	private TestRole role;
	private TestAction action;
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
	}

	public TestRole getRole() {
		return role;
	}

	public void setRole(final TestRole role) {
		this.role = role;
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
