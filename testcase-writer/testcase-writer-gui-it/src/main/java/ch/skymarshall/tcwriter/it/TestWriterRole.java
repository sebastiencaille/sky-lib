package ch.skymarshall.tcwriter.it;

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.annotations.TCRole;

@TCRole(description = "Test writer", humanReadable = "Test writer")
public interface TestWriterRole {

	@TCApi(description = "Select a step", humanReadable = "%s")
	void selectStep(StepSelector selector);

}
