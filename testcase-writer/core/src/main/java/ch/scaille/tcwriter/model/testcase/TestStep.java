package ch.scaille.tcwriter.model.testcase;

import java.util.ArrayList;
import java.util.List;

import ch.scaille.tcwriter.model.dictionary.StepClassifier;
import ch.scaille.tcwriter.model.dictionary.TestAction;
import ch.scaille.tcwriter.model.dictionary.TestActor;
import ch.scaille.tcwriter.model.dictionary.TestRole;

public class TestStep {

	private int ordinal;
	protected TestActor actor = TestActor.NOT_SET;
	protected TestRole role = TestRole.NOT_SET;
	protected TestAction action = TestAction.NOT_SET;
	private final List<TestParameterValue> parametersValue = new ArrayList<>();
	private TestReference reference;
	private StepClassifier classifier = null;

	public TestStep(final int ordinal) {
		this.ordinal = ordinal;
	}

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

	public TestParameterValue getParametersValue(final int index) {
		return parametersValue.get(index);
	}

	public void addParameter(final TestParameterValue parameterValue) {
		this.parametersValue.add(parameterValue);
	}

	public TestReference asNamedReference(final String namedReference, final String description) {
		if (reference == null) {
			reference = createTestReference(this, namedReference, description);
		}
		return reference;
	}

	public TestReference getReference() {
		return reference;
	}

	public TestStep duplicate() {
		final var newTestStep = createTestStep();
		newTestStep.setActor(actor);
		newTestStep.setAction(action);
		parametersValue.stream().forEach(p -> newTestStep.addParameter(p.duplicate()));
		return newTestStep;
	}

	protected TestStep createTestStep() {
		return new TestStep(-1);
	}
	
	protected TestReference createTestReference(TestStep testStep, String namedReference, String description) {
		return new TestReference(testStep, namedReference, description);
	}
	
	
	@Override
	public String toString() {
		return actor.getName() + "." + action.getName();
	}

	public StepClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(StepClassifier classifier) {
		this.classifier = classifier;
	}

	public void fixClassifier() {
		if (classifier != null && List.of(action.getAllowedClassifiers()).contains(classifier)) {
			return;
		}
		setClassifier(action.getAllowedClassifiers()[0]);
	}

}
