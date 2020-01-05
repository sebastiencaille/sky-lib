package ch.skymarshall.tcwriter.it;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.annotations.TCRole;

@TCRole(description = "Test writer", humanReadable = "test writer")
public interface TestWriterRole {

	@TCApi(description = "Select a step", humanReadable = "%s")
	void selectStep(StepSelector selector);

	@TCApi(description = "Update the step", humanReadable = "//%s, //edit the step (%s) and apply the edition")
	void updateStep(StepSelector selector, StepEdition edition);

	@TCApi(description = "Check the Human Readable text", humanReadable = "//%s and //check that the human readable text is \"%s\"")
	void checkHumanReadable(StepSelector selector, String humanReadable);

}
