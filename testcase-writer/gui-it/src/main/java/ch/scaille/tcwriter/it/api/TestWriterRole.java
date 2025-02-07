package ch.scaille.tcwriter.it.api;

import ch.scaille.tcwriter.annotations.TCAction;
import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.annotations.TCCheck;
import ch.scaille.tcwriter.annotations.TCRole;

@TCRole(description = "Test writer", humanReadable = "test writer")
@TCActors("testWriter|TestWriterRole")
public interface TestWriterRole {

	@TCApi(description = "Select a step", humanReadable = "I %s")
	@TCAction
	default void selectStep(StepSelector selector) {
		doSelectStep(selector);
	}

	void doSelectStep(StepSelector selector);

	@TCApi(description = "Edit the step", humanReadable = "I //%s, //edit the step (%s)")
	@TCAction
	default void editStep(StepSelector selector, StepEdition edition) {
		doEditStep(selector, edition);
	}

	void doEditStep(StepSelector selector, StepEdition edition);

	@TCApi(description = "Update the step", humanReadable = "I //%s, //edit the step (%s) and apply the changes")
	@TCAction
	default void updateStep(StepSelector selector, StepEdition edition) {
		doUpdateStep(selector, edition);
	}

	void doUpdateStep(StepSelector selector, StepEdition edition);

	@TCApi(description = "Verify the content of the step", humanReadable = "I //%s and //verify that the step contains \"%s\"")
	@TCCheck
	default void assertStepContent(StepSelector selector, StepEdition edition) {
		doAssertStepContent(selector, edition);
	}

	void doAssertStepContent(StepSelector selector, StepEdition edition);

	@TCApi(description = "Verify the Human Readable text", humanReadable = "I //%s and //verify that the human readable text is \"%s\"")
	@TCCheck
	default void assertHumanReadable(StepSelector selector, String humanReadable) {
		doAssertHumanReadable(selector, humanReadable);
	}

	void doAssertHumanReadable(StepSelector selector, String humanReadable);

	@TCApi(description = "Update the step parameters", humanReadable = "I //%s, //edit %s (%s) and apply the changes")
	@TCAction
	default void updateParameter(ParameterSelector selector, ParameterValue value) {
		doUpdateParameter(selector, value);
	}

	void doUpdateParameter(ParameterSelector selector, ParameterValue value);

	@TCApi(description = "Manage the test", humanReadable = "I %s")
	@TCAction
	default void manageTest(MainFrameAction action) {
		doManageTest(action);
	}

	void doManageTest(MainFrameAction action);

}
