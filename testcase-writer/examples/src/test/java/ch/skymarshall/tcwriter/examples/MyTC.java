// File generated from template
package ch.skymarshall.tcwriter.examples;

import ch.skymarshall.tcwriter.examples.api.interfaces.dto.*;
import ch.skymarshall.tcwriter.examples.api.interfaces.navigators.*;
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
		// Step 0 - ch.skymarshall.tcwriter.generators.model.TestActor@540da1f7: Buy an item
		//    ch.skymarshall.tcwriter.examples.api.interfaces.navigators.BuyActionNavigator
		//    ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem
		ch.skymarshall.tcwriter.examples.api.interfaces.navigators.BuyActionNavigator var0 = BuyActionNavigator.inLocalShop();
		ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem var1 = TestItem.coffeeMachine();
		var1.setBrandName("Cheap");
		customer.buy(var0, var1);
		
		// Step 1 - ch.skymarshall.tcwriter.generators.model.TestActor@540da1f7: check the delivered package
		//    ch.skymarshall.tcwriter.examples.api.interfaces.navigators.HandleActionNavigator
		//    ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem
		ch.skymarshall.tcwriter.examples.api.interfaces.navigators.HandleActionNavigator var2 = HandleActionNavigator.fromShop();
		ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem var3 = TestItem.coffeeMachineOfBrand("DeLuxe");
		customer.checkPackage(var2, var3);
		
		// Step 2 - ch.skymarshall.tcwriter.generators.model.TestActor@540da1f7: Resell the item
		customer.resellOwnedItem(10);
		
		// Step 3 - ch.skymarshall.tcwriter.generators.model.TestActor@540da1f7: Look for another brand
		java.lang.String anotherBrand = customer.findAnotherBrand();
		
		// Step 4 - ch.skymarshall.tcwriter.generators.model.TestActor@540da1f7: Keep a note
		customer.keepNote(anotherBrand);
		
		
	}

}