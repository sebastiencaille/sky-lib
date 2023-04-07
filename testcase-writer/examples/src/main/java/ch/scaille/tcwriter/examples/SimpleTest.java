package ch.scaille.tcwriter.examples;

import static ch.scaille.tcwriter.examples.api.interfaces.selectors.BuyingLocationSelector.inLocalShop;
import static ch.scaille.tcwriter.examples.api.interfaces.selectors.BuyingLocationSelector.onInternet;
import static ch.scaille.tcwriter.examples.api.interfaces.selectors.PackageDeliverySelector.deliveredItem;
import static ch.scaille.tcwriter.examples.api.interfaces.selectors.PackageDeliverySelector.fromShop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.scaille.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.scaille.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.scaille.tcwriter.examples.api.interfaces.dto.TestItem;
import ch.scaille.tcwriter.recorder.RecorderTestActors;

@SuppressWarnings("java:S5960")
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
		final var testedService = new ExampleService();
		customer = RecorderTestActors.register(new CustomerTestRole(testedService), "customer", null);
		deliveryGuy = RecorderTestActors.register(new DeliveryTestRole(testedService), "deliveryGuy", null);
	}

	@Test
	public void testNormalCase() {

		customer.buy(onInternet("https://somewebsite"), coffeeMachine);
		deliveryGuy.deliverItem();
		customer.checkPackage(deliveredItem(), coffeeMachine);
		customer.resellOwnedItem(10);

		customer.buy(inLocalShop(), teaPot);
		customer.checkPackage(fromShop(), teaPot);
		customer.resellOwnedItem(10);

		final var newBrand = customer.findAnotherBrand();
		customer.keepNote(newBrand);
	}

	@Test
	public void testFailureCase() {
		customer.buy(inLocalShop(), coffeeMachine);
		customer.checkPackage(fromShop(), teaPot);
		Assertions.assertThrows(AssertionError.class, () -> customer.resellOwnedItem(10));
	}
}
