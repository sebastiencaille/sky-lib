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
		// Step 1: As customer, I go on internet and buy a coffee machine of brand "OldSchool" (ISO: yes)
		testExecutionController.beforeStepExecution(1);
		ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyItemSelector var1 = BuyItemSelector.fromInternet();
		ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem var2 = TestItem.coffeeMachineOfBrand("OldSchool");
		var2.setISO();
		customer.buy(var1, var2);
		
		testExecutionController.afterStepExecution(1);
		
		// Step 2: As delivery company, I delivers the item
		testExecutionController.beforeStepExecution(2);
		deliveryGuy.deliverItem();
		
		testExecutionController.afterStepExecution(2);
		
		// Step 3: As customer, I get the delivered package and check that the packaged item is a coffee machine of brand "OldSchool" (ISO: yes)
		testExecutionController.beforeStepExecution(3);
		ch.skymarshall.tcwriter.examples.api.interfaces.selectors.HandlePackageSelector var5 = HandlePackageSelector.deliveredItem();
		ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem var6 = TestItem.coffeeMachineOfBrand("OldSchool");
		var6.setISO();
		customer.checkPackage(var5, var6);
		
		testExecutionController.afterStepExecution(3);
		
		// Step 4: As customer, I resell the item for 10$
		testExecutionController.beforeStepExecution(4);
		customer.resellOwnedItem(10);
		
		testExecutionController.afterStepExecution(4);
		
		// Step 5: As customer, I go in a local shop and buy a tea pot
		testExecutionController.beforeStepExecution(5);
		ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyItemSelector var9 = BuyItemSelector.inLocalShop();
		ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem var10 = TestItem.teaPot();
		customer.buy(var9, var10);
		
		testExecutionController.afterStepExecution(5);
		
		// Step 6: As customer, I get the package bought at the shop and check that the packaged item is a tea pot
		testExecutionController.beforeStepExecution(6);
		ch.skymarshall.tcwriter.examples.api.interfaces.selectors.HandlePackageSelector var13 = HandlePackageSelector.fromShop();
		ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem var14 = TestItem.teaPot();
		customer.checkPackage(var13, var14);
		
		testExecutionController.afterStepExecution(6);
		
		// Step 7: As customer, I resell the item for 10$
		testExecutionController.beforeStepExecution(7);
		customer.resellOwnedItem(10);
		
		testExecutionController.afterStepExecution(7);
		
		
	}

}