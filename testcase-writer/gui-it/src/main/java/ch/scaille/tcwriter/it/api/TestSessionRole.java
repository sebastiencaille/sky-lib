package ch.scaille.tcwriter.it.api;

import ch.scaille.tcwriter.annotations.TCAction;
import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.annotations.TCCheck;
import ch.scaille.tcwriter.annotations.TCRole;

@TCRole(description = "Test Session", humanReadable = "test session")
public interface TestSessionRole {

	@TCApi(description = "Inject basic test", humanReadable = "I inject a basic test")
	@TCAction(preparationOnly = true)
	void injectBasicTest();

	@TCApi(description = "Check basic test", humanReadable = "I check the basic test")
	@TCCheck
	void checkBasicTest();
}
