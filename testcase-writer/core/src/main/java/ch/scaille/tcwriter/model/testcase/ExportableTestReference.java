package ch.scaille.tcwriter.model.testcase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.scaille.tcwriter.model.ExportReference;

@JsonIgnoreProperties({ "step" })
public class ExportableTestReference extends TestReference {
	
	public ExportableTestReference() {
		this(ExportableTestStep.EMPTY_STEP, null, null);
	}
	
	public ExportableTestReference(final TestStep step, final String name, final String description) {
		super(step, name, description);
	}
	
	public String getDescription() {
		return description;
	}
	
	@JsonProperty
	public ExportReference getTestStepRef() {
		return new ExportReference(Integer.toString(step.getOrdinal()));
	}

	public void setTestStepRef(final ExportReference ref) {
		ref.setRestoreAction((testCase, id) -> step = testCase.getSteps().get(Integer.parseInt(id) - 1));
	}
	

}
