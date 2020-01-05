package ch.skymarshall.tcwriter.it;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.annotations.TCRole;

@TCRole(description = "Test writer", humanReadable = "test writer")
public interface TestWriterRole {

	@TCApi(description = "Select a step", humanReadable = "%s")
	void selectStep(StepSelector selector);

	@TCApi(description = "Edit the step", humanReadable = "//%s, //edit the step (%s)")
	void editStep(StepSelector selector, StepEdition edition);

	@TCApi(description = "Update the step", humanReadable = "//%s, //edit the step (%s) and apply the edition")
	void updateStep(StepSelector selector, StepEdition edition);

	@TCApi(description = "Check the content of the step", humanReadable = "//%s and //check that the step contains \"%s\"")
	void checkStep(StepSelector selector, StepEdition edition);

	@TCApi(description = "Check the Human Readable text", humanReadable = "//%s and //check that the human readable text is \"%s\"")
	void checkHumanReadable(StepSelector selector, String humanReadable);

	@TCApi(description = "Update the step parameters", humanReadable = "%s (%s) and apply the edition")
	void updateParameter(ParameterSelector selector, ParameterValue value);

}
