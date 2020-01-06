// File generated from template
package ch.skymarshall.tcwriter.examples;

import ch.skymarshall.tcwriter.examples.api.interfaces.dto.*;
import ch.skymarshall.tcwriter.examples.api.interfaces.selectors.*;
import ch.skymarshall.tcwriter.examples.api.interfaces.*;
import ch.skymarshall.tcwriter.recording.*;

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
	public void testCase() {
		
	}

}