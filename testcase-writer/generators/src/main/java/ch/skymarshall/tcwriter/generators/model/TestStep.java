package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.List;

public class TestStep {

	private int ordinal;
	private TestActor actor;
	private TestRole role;
	private TestMethod stepMethod;
	private final List<TestValue> stepParameters = new ArrayList<>();

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

	public TestMethod getMethod() {
		return stepMethod;
	}

	public void setMethod(final TestMethod stepMethod) {
		this.stepMethod = stepMethod;
	}

	public List<TestValue> getParameters() {
		return stepParameters;
	}

	public void addParameter(final TestValue testValue) {
		this.stepParameters.add(testValue);
	}

}
