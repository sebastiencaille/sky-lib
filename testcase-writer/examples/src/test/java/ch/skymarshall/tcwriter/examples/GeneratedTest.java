// File generated from template
package ch.skymarshall.tcwriter.examples;

import ch.skymarshall.tcwriter.examples.api.interfaces.dto.*;
import ch.skymarshall.tcwriter.examples.api.interfaces.selectors.*;
import ch.skymarshall.tcwriter.examples.api.interfaces.*;
import ch.skymarshall.tcwriter.test.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class GeneratedTest {

	private ITestExecutionController testExecutionController;
	
	@org.junit.Rule
	public org.junit.rules.TestWatcher testWatcher = new org.junit.rules.TestWatcher() {
		@Override
		protected void failed(final Throwable e, final org.junit.runner.Description description) {
			super.failed(e, description);
			testExecutionController.notifyError(e);
		}
	};
	@org.junit.Before
	public void prepareController() throws IOException {
		testExecutionController = TestExecutionController.controller();
	}
	

	private CustomerTestRole customer;
	private DeliveryTestRole deliveryGuy;

	@Before
	public void prepareApis() {
		final ExampleService testedService = new ExampleService();
		customer = new CustomerTestRole(testedService);
		deliveryGuy = new DeliveryTestRole(testedService);
	}
	
	@Test
	public void testCase() throws Exception {
		testExecutionController.beforeTestExecution();
		
	}

}