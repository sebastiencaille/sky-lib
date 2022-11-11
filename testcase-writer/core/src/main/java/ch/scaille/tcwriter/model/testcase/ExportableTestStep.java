package ch.scaille.tcwriter.model.testcase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.scaille.tcwriter.model.ExportReference;
import ch.scaille.tcwriter.model.dictionary.TestAction;

@JsonIgnoreProperties({"actor", "role", "action" })
public class ExportableTestStep extends TestStep {
	
	public static final ExportableTestStep EMPTY_STEP = new ExportableTestStep();
	
	public ExportableTestStep() {
		super(-1);
	}
	
	public ExportableTestStep(int ordinal) {
		super(ordinal);
	}
	
	@Override
	protected TestStep createTestStep() {
		return new ExportableTestStep(-1);
	}

	@Override
	protected TestReference createTestReference(TestStep testStep, String namedReference, String description) {
		return new ExportableTestReference(testStep, namedReference, description);
	}
	
	@JsonProperty
	public ExportReference getActorRef() {
		return new ExportReference(actor);
	}

	public void setActorRef(final ExportReference ref) {
		ref.setRestoreAction((tc, id) -> actor = tc.getDictionary().getActors().get(id));
	}

	@JsonProperty
	public ExportReference getRoleRef() {
		return new ExportReference(role);
	}

	public void setRoleRef(final ExportReference ref) {
		ref.setRestoreAction((tc, id) -> role = tc.getDictionary().getRoles().get(id));
	}

	@JsonProperty
	public ExportReference getActionRef() {
		return new ExportReference(action);
	}

	public void setActionRef(final ExportReference ref) {
		ref.setRestoreAction((tc, id) -> action = (TestAction) ((ExportableTestCase)tc).getRestoreValue(id));
	}
	
}
