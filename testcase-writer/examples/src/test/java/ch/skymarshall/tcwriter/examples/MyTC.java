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
		testExecutionController.beforeStepExecution(1);
		// Step 1 - ch.skymarshall.tcwriter.generators.model.TestActor@540de4ad: Buy an item/buy
		//    ch.skymarshall.tcwriter.examples.api.interfaces.navigators.BuyActionNavigator
		//    ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem
		ch.skymarshall.tcwriter.examples.api.interfaces.navigators.BuyActionNavigator var0 = BuyActionNavigator.inLocalShop();
		ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem var1 = TestItem.coffeeMachine();
		var1.setBrandName("DeLuxeBrand");
		customer.buy(var0, var1);
		
		testExecutionController.afterStepExecution(1);
		testExecutionController.beforeStepExecution(2);
		// Step 2 - ch.skymarshall.tcwriter.generators.model.TestActor@540de4ad: check the packaged item/check that the packaged item is
		//    ch.skymarshall.tcwriter.examples.api.interfaces.navigators.HandleActionNavigator
		//    ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem
		ch.skymarshall.tcwriter.examples.api.interfaces.navigators.HandleActionNavigator var2 = HandleActionNavigator.fromShop();
		ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem var3 = TestItem.coffeeMachineOfBrand("DeLuxeBrand");
		customer.checkPackage(var2, var3);
		
		testExecutionController.afterStepExecution(2);
		testExecutionController.beforeStepExecution(3);
		// Step 3 - ch.skymarshall.tcwriter.generators.model.TestActor@540de4ad: Resell the item/resell the item
		customer.resellOwnedItem(10);
		
		testExecutionController.afterStepExecution(3);
		testExecutionController.beforeStepExecution(4);
		// Step 4 - ch.skymarshall.tcwriter.generators.model.TestActor@540de4ad: Look for another brand/find another brand
		java.lang.String anotherBrand = customer.findAnotherBrand();
		
		testExecutionController.afterStepExecution(4);
		testExecutionController.beforeStepExecution(5);
		// Step 5 - ch.skymarshall.tcwriter.generators.model.TestActor@540de4ad: Keep a note/keep a note
		customer.keepNote(anotherBrand);
		
		testExecutionController.afterStepExecution(5);
		
	}

}