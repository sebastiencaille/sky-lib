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

	@TCApi(description = "Check the content of the step", humanReadable = "I //%s and //check that the step contains \"%s\"")
	@TCCheck
	void checkStep(StepSelector selector, StepEdition edition);

	@TCApi(description = "Check the Human Readable text", humanReadable = "I //%s and //check that the human readable text is \"%s\"")
	@TCCheck
	void checkHumanReadable(StepSelector selector, String humanReadable);

	@TCApi(description = "Update the step parameters", humanReadable = "I //%s, //edit %s (%s) and apply the changes")
	@TCAction
	void updateParameter(ParameterSelector selector, ParameterValue value);

	@TCApi(description = "Action on main frame", humanReadable = "I %s")
	@TCAction
	void mainFrameAction(MainFrameAction action);

}
