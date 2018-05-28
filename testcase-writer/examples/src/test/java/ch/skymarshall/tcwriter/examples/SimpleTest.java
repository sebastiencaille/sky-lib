package ch.skymarshall.tcwriter.examples;

import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyActionSelector.fromInternet;
import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyActionSelector.inLocalShop;
import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.HandleActionSelector.deliveredItem;
import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.HandleActionSelector.fromShop;

import org.junit.Before;
import org.junit.Test;

import ch.skymarshall.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem;
import junit.framework.AssertionFailedError;

public class SimpleTest {

	private final TestItem coffeeMachine = TestItem.coffeeMachine();
	private final TestItem teaPot = TestItem.teaPot();
	private CustomerTestRole customer;
	private DeliveryTestRole delivery;

	@Before
	public void prepareApis() {
		final ExampleService testedService = new ExampleService();
		customer = new CustomerTestRole(testedService);
		delivery = new DeliveryTestRole(testedService);
	}

	@Test
	public void testNormalCase() {

		customer.buy(fromInternet(), coffeeMachine);
		delivery.deliverItem();
		customer.handleAndCheckPackage(deliveredItem(), coffeeMachine);
		customer.resellOwnedItem();

		customer.buy(inLocalShop(), teaPot);
		customer.handleAndCheckPackage(fromShop(), teaPot);
		customer.resellOwnedItem();
	}

	@Test(expected = AssertionFailedError.class)
	public void testFailureCase() {
		final CustomerTestRole api = new CustomerTestRole(new ExampleService());

		api.buy(inLocalShop(), coffeeMachine);
		api.handleAndCheckPackage(fromShop(), teaPot);
		api.resellOwnedItem();

	}

}
