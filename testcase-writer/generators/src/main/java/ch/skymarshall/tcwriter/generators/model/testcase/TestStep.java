package ch.skymarshall.tcwriter.generators.model.testcase;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.skymarshall.tcwriter.generators.model.ExportReference;
import ch.skymarshall.tcwriter.generators.model.testapi.TestAction;
import ch.skymarshall.tcwriter.generators.model.testapi.TestActor;
import ch.skymarshall.tcwriter.generators.model.testapi.TestRole;

public class TestStep {

	private int ordinal;
	@JsonIgnore
	private TestActor actor = TestActor.NOT_SET;
	@JsonIgnore
	private TestRole role = TestRole.NOT_SET;
	@JsonIgnore
	private TestAction action = TestAction.NOT_SET;
	private final List<TestParameterValue> parametersValue = new ArrayList<>();
	private TestReference reference;

	protected TestStep() {
		this.ordinal = -1;
	}

	public TestStep(final int ordinal) {
		this.ordinal = ordinal;
	}

	@JsonProperty
	public ExportReference getActorRef() {
		return new ExportReference(actor);
	}

	public void setActorRef(final ExportReference ref) {
		ref.setRestoreAction((tc, id) -> actor = tc.getModel().getActors().get(id));
	}

	@JsonProperty
	public ExportReference getRoleRef() {
		return new ExportReference(role);
	}

	public void setRoleRef(final ExportReference ref) {
		ref.setRestoreAction((tc, id) -> role = tc.getModel().getRoles().get(id));
	}

	@JsonProperty
	public ExportReference getActionRef() {
		return new ExportReference(action);
	}

	public void setActionRef(final ExportReference ref) {
		ref.setRestoreAction((tc, id) -> action = (TestAction) tc.getRestoreValue(id));
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
		reference = new TestReference(this, namedReference, getAction().getReturnType(), description);
		return reference;
	}

	public TestReference getReference() {
		return reference;
	}

	public TestStep duplicate() {
		final TestStep newTestStep = new TestStep();
		newTestStep.setActor(actor);
		newTestStep.setAction(action);
		parametersValue.stream().forEach(p -> newTestStep.addParameter(p.duplicate()));
		return newTestStep;
	}

	@Override
	public String toString() {
		return actor.getName() + "." + action.getName();
	}

}
