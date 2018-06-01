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
		// Step 0 - ch.skymarshall.tcwriter.generators.model.TestActor@12bc6874: Look for another brand
		java.lang.String anotherBrand = customer.findAnotherBrand();
		
		// Step 0 - ch.skymarshall.tcwriter.generators.model.TestActor@12bc6874: Keep a note
		customer.keepNote(anotherBrand);
		
		
	}

}