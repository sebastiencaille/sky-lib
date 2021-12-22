package ch.scaille.tcwriter.it.api;

import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.annotations.TCRole;

@TCRole(description = "Test Session", humanReadable = "test session")
public interface TestSessionRole {

	@TCApi(description = "Inject basic test", humanReadable = "I inject a basic test")
	void injectBasicTest();

	@TCApi(description = "Check basic test", humanReadable = "I check the basic test")
	void checkBasicTest();
}
