// File generated from template
package ch.skymarshall.tcwriter.examples;

import ch.skymarshall.tcwriter.examples.api.interfaces.dto.*;
import ch.skymarshall.tcwriter.examples.api.interfaces.selectors.*;
import ch.skymarshall.tcwriter.examples.api.interfaces.*;

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
	public void testCase() {
		// Step 0 - ch.skymarshall.tcwriter.generators.model.TestActor@16aa0a0a: Buy an item
		//    null
		//    null
		ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyActionSelector var0 = BuyActionSelector.inLocalShop();
		ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem var1 = TestItem.coffeeMachine();
		var1.setBrandName("Cheap");
		customer.buy(var0, var1);
		
		// Step 0 - ch.skymarshall.tcwriter.generators.model.TestActor@16aa0a0a: handle the item package and check it's content
		//    null
		//    null
		ch.skymarshall.tcwriter.examples.api.interfaces.selectors.HandleActionSelector var2 = HandleActionSelector.fromShop();
		ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem var3 = TestItem.coffeeMachineOfBrand("DeLuxe");
		customer.handleAndCheckPackage(var2, var3);
		
		// Step 0 - ch.skymarshall.tcwriter.generators.model.TestActor@16aa0a0a: Resell the item
		customer.resellOwnedItem(10);
		
		
	}

}