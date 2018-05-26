package ch.skymarshall.tcwriter.generators.model;

import java.util.ArrayList;
import java.util.List;

public class TestStep {

	private int ordinal;
	private TestActor stepActor;
	private TestMethod stepMethod;
	private final List<TestValue> stepParameters = new ArrayList<>();

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(final int ordinal) {
		this.ordinal = ordinal;
	}

	public TestActor getActor() {
		return stepActor;
	}

	public void setActor(final TestActor stepActor) {
		this.stepActor = stepActor;
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
