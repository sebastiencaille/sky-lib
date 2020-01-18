package ch.skymarshall.tcwriter.it;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.annotations.TCRole;

@TCRole(description = "Test writer", humanReadable = "test writer")
public interface TestWriterRole {

	@TCApi(description = "Select a step", humanReadable = "I %s")
	void selectStep(StepSelector selector);

	@TCApi(description = "Edit the step", humanReadable = "I //%s, //edit the step (%s)")
	void editStep(StepSelector selector, StepEdition edition);

	@TCApi(description = "Update the step", humanReadable = "I //%s, //edit the step (%s) and apply the changes")
	void updateStep(StepSelector selector, StepEdition edition);

	@TCApi(description = "Check the content of the step", humanReadable = "I //%s and //check that the step contains \"%s\"")
	void checkStep(StepSelector selector, StepEdition edition);

	@TCApi(description = "Check the Human Readable text", humanReadable = "I //%s and //check that the human readable text is \"%s\"")
	void checkHumanReadable(StepSelector selector, String humanReadable);

	@TCApi(description = "Update the step parameters", humanReadable = "I //%s, //edit %s (%s) and apply the changes")
	void updateParameter(ParameterSelector selector, ParameterValue value);

}
