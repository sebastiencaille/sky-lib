// File generated from template
package ch.skymarshall.tcwriter.examples;

import ch.skymarshall.tcwriter.examples.api.interfaces.dto.*;
import ch.skymarshall.tcwriter.examples.api.interfaces.selectors.*;
import ch.skymarshall.tcwriter.examples.api.interfaces.*;
import ch.skymarshall.tcwriter.recording.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class GeneratedTest {

	private CustomerTestRole customer;
	private DeliveryTestRole deliveryGuy;

	@Before
	public void prepareApis() {
		final ExampleService testedService = new ExampleService();
		customer = new CustomerTestRole(testedService);
		TestActors.register(customer, "customer", null);
		deliveryGuy = new DeliveryTestRole(testedService);
		TestActors.register(deliveryGuy, "deliveryGuy", null);
		ch.skymarshall.tcwriter.TestFeature.aspectjRecorder().enable();
		ch.skymarshall.tcwriter.TestFeature.aspectjStepping(); // don't enable by default
	}
	
	@Test
	public void testCase() throws Exception {
		// Step 1: As customer, I go on internet and buy a coffee machine of brand "OldSchool" (ISO: yes)
		ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyingLocationSelector var1 = BuyingLocationSelector.onInternet();
		ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem var2 = TestItem.coffeeMachineOfBrand("OldSchool");
		var2.setISO();
		customer.buy(var1, var2);
		
		// Step 2: As delivery company, I delivers the item
		deliveryGuy.deliverItem();
		
		// Step 3: As customer, I get the delivered package and check that the packaged item is a coffee machine of brand "OldSchool" (ISO: yes)
		ch.skymarshall.tcwriter.examples.api.interfaces.selectors.PackageDeliverySelector var5 = PackageDeliverySelector.deliveredItem();
		ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem var6 = TestItem.coffeeMachineOfBrand("OldSchool");
		var6.setISO();
		customer.checkPackage(var5, var6);
		
		// Step 4: As customer, I resell the item for 10$
		customer.resellOwnedItem(10);
		
		// Step 5: As customer, I go in a local shop and buy a tea pot
		ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyingLocationSelector var9 = BuyingLocationSelector.inLocalShop();
		ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem var10 = TestItem.teaPot();
		customer.buy(var9, var10);
		
		// Step 6: As customer, I get the package bought at the shop and check that the packaged item is a tea pot
		ch.skymarshall.tcwriter.examples.api.interfaces.selectors.PackageDeliverySelector var13 = PackageDeliverySelector.fromShop();
		ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem var14 = TestItem.teaPot();
		customer.checkPackage(var13, var14);
		
		// Step 7: As customer, I resell the item for 10$
		customer.resellOwnedItem(10);
		
		// Step 8: As customer, I find another brand
		java.lang.String ref8 = customer.findAnotherBrand();
		
		// Step 9: As customer, I keep the note "[Value of step 8: MidClass]"
		customer.keepNote(ref8);
		
		
	}

}