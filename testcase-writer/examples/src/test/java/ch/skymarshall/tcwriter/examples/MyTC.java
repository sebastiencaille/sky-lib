// File generated from template
package ch.skymarshall.tcwriter.examples;

import ch.skymarshall.tcwriter.examples.api.interfaces.dto.*;
import ch.skymarshall.tcwriter.examples.api.interfaces.navigators.*;
import ch.skymarshall.tcwriter.examples.api.interfaces.*;
import ch.skymarshall.tcwriter.test.*;

import org.junit.Before;
import org.junit.Test;

public class MyTC {

	private CustomerTestRole customer;
	private DeliveryTestRole delivery;

	@Before
	public void prepareApis() {
		final ExampleService testedService = new ExampleService();
		customer = new CustomerTestRole(testedService);
		delivery = new DeliveryTestRole(testedService);
	}
	
	@Test
	public void testCase() throws Exception {
		TestExecutionController testExecutionController = new TestExecutionController();
		testExecutionController.beforeTestExecution();
		// Step 1: As customer, I buy in a local shop: a coffee machine of brand: DeLuxeBrand
		testExecutionController.beforeStepExecution(1);
		ch.skymarshall.tcwriter.examples.api.interfaces.navigators.BuyActionNavigator var0 = BuyActionNavigator.inLocalShop();
		ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem var1 = TestItem.coffeeMachine();
		var1.setBrandName("DeLuxeBrand");
		customer.buy(var0, var1);
		
		testExecutionController.afterStepExecution(1);
		// Step 2: As customer, I check that the packaged item is the item bought at the shop: a coffee machine of brand DeLuxeBrand
		testExecutionController.beforeStepExecution(2);
		ch.skymarshall.tcwriter.examples.api.interfaces.navigators.HandleActionNavigator var2 = HandleActionNavigator.fromShop();
		ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem var3 = TestItem.coffeeMachineOfBrand("DeLuxeBrand");
		customer.checkPackage(var2, var3);
		
		testExecutionController.afterStepExecution(2);
		// Step 3: As customer, I resell the item (in $): 10
		testExecutionController.beforeStepExecution(3);
		customer.resellOwnedItem(10);
		
		testExecutionController.afterStepExecution(3);
		// Step 4: As customer, I find another brand
		testExecutionController.beforeStepExecution(4);
		java.lang.String anotherBrand = customer.findAnotherBrand();
		
		testExecutionController.afterStepExecution(4);
		// Step 5: As customer, I keep a note: another brand (from step 4)
		testExecutionController.beforeStepExecution(5);
		customer.keepNote(anotherBrand);
		
		testExecutionController.afterStepExecution(5);
		
	}

}