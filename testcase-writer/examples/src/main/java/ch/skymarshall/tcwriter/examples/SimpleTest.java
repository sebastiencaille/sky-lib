package ch.skymarshall.tcwriter.examples;

import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyingLocationSelector.inLocalShop;
import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyingLocationSelector.onInternet;
import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.PackageDeliverySelector.deliveredItem;
import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.PackageDeliverySelector.fromShop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.skymarshall.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem;
import ch.skymarshall.tcwriter.recording.TestActors;

public class SimpleTest {

	private final TestItem coffeeMachine = TestItem.coffeeMachineOfBrand("OldSchool");
	private final TestItem teaPot = TestItem.teaPot();
	private CustomerTestRole customer;
	private DeliveryTestRole deliveryGuy;

	public SimpleTest() {
		coffeeMachine.setISO();
	}

	@BeforeEach
	public void initActors() {
		final ExampleService testedService = new ExampleService();
		customer = new CustomerTestRole(testedService);
		TestActors.register(customer, "customer", null);
		deliveryGuy = new DeliveryTestRole(testedService);
		TestActors.register(deliveryGuy, "deliveryGuy", null);
	}

	@Test
	public void testNormalCase() {

		customer.buy(onInternet("https://truc"), coffeeMachine);
		deliveryGuy.deliverItem();
		customer.checkPackage(deliveredItem(), coffeeMachine);
		customer.resellOwnedItem(10);

		customer.buy(inLocalShop(), teaPot);
		customer.checkPackage(fromShop(), teaPot);
		customer.resellOwnedItem(10);

		final String newBrand = customer.findAnotherBrand();
		customer.keepNote(newBrand);
	}

	public void testFailureCase() {
		customer.buy(inLocalShop(), coffeeMachine);
		customer.checkPackage(fromShop(), teaPot);
		Assertions.assertThrows(AssertionError.class, () -> customer.resellOwnedItem(10));
	}
}
