package ch.skymarshall.tcwriter.examples;

import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyItemSelector.fromInternet;
import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyItemSelector.inLocalShop;
import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.HandlePackageSelector.deliveredItem;
import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.HandlePackageSelector.fromShop;

import org.junit.Before;
import org.junit.Test;

import ch.skymarshall.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem;
import ch.skymarshall.tcwriter.recording.TestActors;

public class ExampleTest {

	private final TestItem coffeeMachine = TestItem.coffeeMachineOfBrand("OldSchool");
	private final TestItem teaPot = TestItem.teaPot();
	private CustomerTestRole customer;
	private DeliveryTestRole deliveryGuy;

	public ExampleTest() {
		coffeeMachine.setISO();
	}

	@Before
	public void initActors() {
		final ExampleService testedService = new ExampleService();
		customer = new CustomerTestRole(testedService);
		TestActors.register(customer, "customer", null);
		deliveryGuy = new DeliveryTestRole(testedService);
		TestActors.register(deliveryGuy, "deliveryGuy", null);
	}

	@Test
	public void testNormalCase() {

		customer.buy(fromInternet(), coffeeMachine);
		deliveryGuy.deliverItem();
		customer.checkPackage(deliveredItem(), coffeeMachine);
		customer.resellOwnedItem(10);

		customer.buy(inLocalShop(), teaPot);
		customer.checkPackage(fromShop(), teaPot);
		customer.resellOwnedItem(10);

		final String newBrand = customer.findAnotherBrand();
		customer.keepNote(newBrand);
	}

	@Test(expected = AssertionError.class)
	public void testFailureCase() {
		customer.buy(inLocalShop(), coffeeMachine);
		customer.checkPackage(fromShop(), teaPot);
		customer.resellOwnedItem(10);
	}
}
