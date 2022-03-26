// File generated from template 2022/03/26 01:45:04
package ch.scaille.tcwriter.examples;

import ch.scaille.tcwriter.examples.api.interfaces.dto.*;
import ch.scaille.tcwriter.examples.api.interfaces.selectors.*;
import ch.scaille.tcwriter.examples.api.interfaces.*;
import ch.scaille.tcwriter.recorder.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GeneratedTest {

	private CustomerTestRole customer;
	private DeliveryTestRole deliveryGuy;

	@BeforeEach
	public void prepareApis() {
		final ExampleService testedService = new ExampleService();
		customer = new CustomerTestRole(testedService);
		RecorderTestActors.register(customer, "customer", null);
		deliveryGuy = new DeliveryTestRole(testedService);
		RecorderTestActors.register(deliveryGuy, "deliveryGuy", null);
		ch.scaille.tcwriter.TestFeature.aspectjRecorder().enable();
		ch.scaille.tcwriter.TestFeature.aspectjStepping(); // don't enable by default
	}
	
	@Test
	public void testCase() {
		// Step 1: As customer, I go on internet (https://somewebsite) and buy a coffee machine of brand "OldSchool" (ISO: yes)
		ch.scaille.tcwriter.examples.api.interfaces.selectors.BuyingLocationSelector step1_var1 = BuyingLocationSelector.onInternet("https://somewebsite");
		ch.scaille.tcwriter.examples.api.interfaces.dto.TestItem step1_var2 = TestItem.coffeeMachineOfBrand("OldSchool");
		step1_var2.setISO();
		customer.buy(step1_var1, step1_var2);
		
		// Step 2: As delivery guy, I deliver the item
		deliveryGuy.deliverItem();
		
		// Step 3: As customer, I get the delivered package and check that the packaged item is a coffee machine of brand "OldSchool" (ISO: yes)
		ch.scaille.tcwriter.examples.api.interfaces.selectors.PackageDeliverySelector step3_var1 = PackageDeliverySelector.deliveredItem();
		ch.scaille.tcwriter.examples.api.interfaces.dto.TestItem step3_var2 = TestItem.coffeeMachineOfBrand("OldSchool");
		step3_var2.setISO();
		customer.checkPackage(step3_var1, step3_var2);
		
		// Step 4: As customer, I resell the item for 10$
		customer.resellOwnedItem(10);
		
		// Step 5: As customer, I go in a local shop and buy a tea pot
		ch.scaille.tcwriter.examples.api.interfaces.selectors.BuyingLocationSelector step5_var1 = BuyingLocationSelector.inLocalShop();
		ch.scaille.tcwriter.examples.api.interfaces.dto.TestItem step5_var2 = TestItem.teaPot();
		customer.buy(step5_var1, step5_var2);
		
		// Step 6: As customer, I get the package bought at the shop and check that the packaged item is a tea pot
		ch.scaille.tcwriter.examples.api.interfaces.selectors.PackageDeliverySelector step6_var1 = PackageDeliverySelector.fromShop();
		ch.scaille.tcwriter.examples.api.interfaces.dto.TestItem step6_var2 = TestItem.teaPot();
		customer.checkPackage(step6_var1, step6_var2);
		
		// Step 7: As customer, I resell the item for 10$
		customer.resellOwnedItem(10);
		
		// Step 8: As customer, I find another brand
		java.lang.String ref8 = customer.findAnotherBrand();
		
		// Step 9: As customer, I keep the note "[Value of step 8: MidClass]"
		customer.keepNote(ref8);
		
		
	}

}