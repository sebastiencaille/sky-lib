package ch.scaille.tcwriter.it.api;

import ch.scaille.tcwriter.annotations.TCAction;
import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.annotations.TCCheck;
import ch.scaille.tcwriter.annotations.TCRole;

@TCRole(description = "Test Session", humanReadable = "test session")
public interface TestSessionRole {

	@TCApi(description = "Inject test", humanReadable = "I inject the test %s")
	@TCAction(preparationOnly = true)
	default void injectTest(TestContent testContent)  {
		doInjectTest(testContent);
	}

	
	void doInjectTest(TestContent testContent);
	
	@TCApi(description = "Verify test", humanReadable = "I verify the test %s")
	@TCCheck
	default void assertTest(TestContent testContent) {
		doAssertTest(testContent);
	}
	
	void doAssertTest(TestContent testContent);
}
