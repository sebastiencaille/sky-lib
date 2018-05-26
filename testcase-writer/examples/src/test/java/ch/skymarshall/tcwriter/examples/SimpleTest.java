package ch.skymarshall.tcwriter.examples;

import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyActionSelector.fromInternet;
import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyActionSelector.inLocalShop;
import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.HandleActionSelector.deliveredItem;
import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.HandleActionSelector.fromShop;

import org.junit.Before;
import org.junit.Test;

import ch.skymarshall.tcwriter.examples.ExampleService;
import ch.skymarshall.tcwriter.examples.api.interfaces.CustomerTestActor;
import ch.skymarshall.tcwriter.examples.api.interfaces.DeliveryTestActor;
import ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem;

public class SimpleTest {

	private final TestItem coffeeMachine = TestItem.coffeeMachine();
	private final TestItem teaPot = TestItem.teaPot();
	private CustomerTestActor customer;
	private DeliveryTestActor delivery;

	@Before
	public void prepareApis() {
		final ExampleService testedService = new ExampleService();
		customer = new CustomerTestActor(testedService);
		delivery = new DeliveryTestActor(testedService);
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

	@Test
	public void testFailureCase() {
		final CustomerTestActor api = new CustomerTestActor(new ExampleService());

		api.buy(inLocalShop(), coffeeMachine);
		api.handleAndCheckPackage(fromShop(), teaPot);
		api.resellOwnedItem();

	}

}
