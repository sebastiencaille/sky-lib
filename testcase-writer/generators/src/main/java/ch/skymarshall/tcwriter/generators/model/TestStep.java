package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.List;

public class TestStep {

	private int ordinal;
	private TestActor actor;
	private TestRole role;
	private TestAction stepMethod;
	private final List<TestParameterValue> parametersValue = new ArrayList<>();

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
		return stepMethod;
	}

	public void setMethod(final TestAction stepMethod) {
		this.stepMethod = stepMethod;
	}

	public List<TestParameterValue> getParametersValue() {
		return parametersValue;
	}

	public void addParameter(final TestParameterValue testValue) {
		this.parametersValue.add(testValue);
	}

}
