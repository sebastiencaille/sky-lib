package ch.skymarshall.tcwriter.examples;

import static ch.skymarshall.tcwriter.examples.api.interfaces.navigators.BuyActionNavigator.fromInternet;
import static ch.skymarshall.tcwriter.examples.api.interfaces.navigators.BuyActionNavigator.inLocalShop;
import static ch.skymarshall.tcwriter.examples.api.interfaces.navigators.HandleActionNavigator.deliveredItem;
import static ch.skymarshall.tcwriter.examples.api.interfaces.navigators.HandleActionNavigator.fromShop;

import org.junit.Before;
import org.junit.Test;

import ch.skymarshall.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem;
import ch.skymarshall.tcwriter.examples.hmi.ExampleTCWriter;
import ch.skymarshall.tcwriter.generators.TestSummaryVisitor;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;

public class SimpleTest {

	private final TestItem coffeeMachine = TestItem.coffeeMachine();
	private final TestItem teaPot = TestItem.teaPot();
	private CustomerTestRole customer;
	private DeliveryTestRole deliveryGuy;

	@Before
	public void prepareApis() {
		final ExampleService testedService = new ExampleService();
		customer = new CustomerTestRole(testedService);
		deliveryGuy = new DeliveryTestRole(testedService);
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
	}

	@Test(expected = AssertionError.class)
	public void testFailureCase() {
		final CustomerTestRole api = new CustomerTestRole(new ExampleService());

		api.buy(inLocalShop(), coffeeMachine);
		api.checkPackage(fromShop(), teaPot);
		api.resellOwnedItem(10);

	}

	@Test
	public void testSummary() {
		final TestCase testCase = ExampleTCWriter.createTestCase();
		final TestSummaryVisitor testSummaryVisitor = new TestSummaryVisitor(testCase);
		for (final TestStep step : testCase.getSteps()) {
			System.out.println("Step " + step.getOrdinal() + ": " + testSummaryVisitor.process(step));
		}
	}

}
