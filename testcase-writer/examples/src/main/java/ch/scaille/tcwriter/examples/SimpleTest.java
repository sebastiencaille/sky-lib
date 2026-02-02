package ch.scaille.tcwriter.examples;

import static ch.scaille.tcwriter.examples.simple.selectors.BuyingLocationSelector.inLocalShop;
import static ch.scaille.tcwriter.examples.simple.selectors.BuyingLocationSelector.onInternet;
import static ch.scaille.tcwriter.examples.simple.selectors.PackageDeliverySelector.deliveredItem;
import static ch.scaille.tcwriter.examples.simple.selectors.PackageDeliverySelector.fromShop;

import ch.scaille.tcwriter.examples.simple.AbstractSimpleTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.scaille.tcwriter.examples.simple.dto.TestItem;

public class SimpleTest extends AbstractSimpleTest {

	private final TestItem coffeeMachine = TestItem.coffeeMachineOfBrand("OldSchool");
	private final TestItem teaPot = TestItem.teaPot();

	public SimpleTest() {
		coffeeMachine.setISO(true);
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
		Assertions.assertThrows(AssertionError.class, () -> customer.checkPackage(fromShop(), teaPot));
	}
}
