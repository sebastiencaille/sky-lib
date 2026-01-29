package ch.scaille.tcwriter.model.testcase;

import ch.scaille.tcwriter.model.dictionary.StepClassifier;
import ch.scaille.tcwriter.model.dictionary.TestActor;
import ch.scaille.tcwriter.model.dictionary.TestRole;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.scaille.tcwriter.model.ExportReference;
import ch.scaille.tcwriter.model.dictionary.TestAction;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@JsonIgnoreProperties({"actor", "role", "action" })
@NullMarked
public class ExportableTestStep extends TestStep {
	
	public static final ExportableTestStep EMPTY_STEP = new ExportableTestStep();
	
	public ExportableTestStep() {
		super(-1);
	}
	
	public ExportableTestStep(int ordinal) {
		super(ordinal);
	}

	@JsonCreator
	public ExportableTestStep(final int ordinal, List<TestParameterValue> parametersValue, TestReference reference, StepClassifier classifier) {
		super(ordinal, TestActor.NOT_SET, TestRole.NOT_SET, TestAction.NOT_SET, parametersValue, reference, classifier);
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

	/**
	 * When the object is deserialized, provide how to restore the state of the action, according to the saved id
     */
	public void setActionRef(final ExportReference ref) {
		ref.setRestoreAction((tc, id) -> action = (TestAction) ((ExportableTestCase)tc).getRestoreValue(id));
	}
	
}
