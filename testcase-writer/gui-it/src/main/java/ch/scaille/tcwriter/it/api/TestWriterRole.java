package ch.scaille.tcwriter.it.api;

import ch.scaille.tcwriter.annotations.TCAction;
import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.annotations.TCCheck;
import ch.scaille.tcwriter.annotations.TCRole;

@TCRole(description = "Test writer", humanReadable = "test writer")
public interface TestWriterRole {

	@TCApi(description = "Select a step", humanReadable = "I %s")
	@TCAction
	void selectStep(StepSelector selector);

	@TCApi(description = "Edit the step", humanReadable = "I //%s, //edit the step (%s)")
	@TCAction
	void editStep(StepSelector selector, StepEdition edition);

	@TCApi(description = "Update the step", humanReadable = "I //%s, //edit the step (%s) and apply the changes")
	@TCAction
	void updateStep(StepSelector selector, StepEdition edition);

	@TCApi(description = "Verify the content of the step", humanReadable = "I //%s and //verify that the step contains \"%s\"")
	@TCCheck
	void assertStepContent(StepSelector selector, StepEdition edition);

	@TCApi(description = "Verify the Human Readable text", humanReadable = "I //%s and //verify that the human readable text is \"%s\"")
	@TCCheck
	void assertHumanReadable(StepSelector selector, String humanReadable);

	@TCApi(description = "Update the step parameters", humanReadable = "I //%s, //edit %s (%s) and apply the changes")
	@TCAction
	void updateParameter(ParameterSelector selector, ParameterValue value);

	@TCApi(description = "Action on main frame", humanReadable = "I %s")
	@TCAction
	void mainFrameAction(MainFrameAction action);

}
