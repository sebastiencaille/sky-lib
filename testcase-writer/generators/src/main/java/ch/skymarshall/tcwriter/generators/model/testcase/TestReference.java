package ch.skymarshall.tcwriter.generators.model.testcase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.skymarshall.tcwriter.generators.model.ExportReference;
import ch.skymarshall.tcwriter.generators.model.ObjectDescription;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameter;

public class TestReference extends TestParameter {

	@JsonIgnore
	private TestStep step;
	private String description;

	protected TestReference() {
		step = null;
		description = null;
	}

	public TestReference(final TestStep step, final String name, final String type, final String description) {
		super(name, name, ParameterNature.REFERENCE, type);
		this.step = step;
		this.description = description;
	}

	public TestReference rename(final String newName, final String description) {
		super.setName(newName);
		this.description = description;
		return this;
	}

	@JsonProperty
	public ExportReference getTestStepRef() {
		return new ExportReference(Integer.toString(step.getOrdinal()));
	}

	public void setTestStepRef(final ExportReference ref) {
		ref.setRestoreAction((testCase, id) -> step = testCase.getSteps().get(Integer.parseInt(id) - 1));
	}

	public TestStep getStep() {
		return step;
	}

	public ObjectDescription toDescription() {
		return new ObjectDescription(getName() + ": (step " + step.getOrdinal() + ") ", description);
	}
}
