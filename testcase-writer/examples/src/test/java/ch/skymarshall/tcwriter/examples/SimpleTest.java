package ch.skymarshall.tcwriter.examples;

import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyItemSelector.fromInternet;
import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.BuyItemSelector.inLocalShop;
import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.HandlePackageSelector.deliveredItem;
import static ch.skymarshall.tcwriter.examples.api.interfaces.selectors.HandlePackageSelector.fromShop;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.skymarshall.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.dto.TestItem;
import ch.skymarshall.tcwriter.generators.ClassToModelGenerator;
import ch.skymarshall.tcwriter.generators.model.testapi.TestActor;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.generators.recorder.aspectj.AspectjRecorder;
import ch.skymarshall.tcwriter.generators.visitors.HumanReadableVisitor;

public class SimpleTest {

	private final TestItem coffeeMachine = TestItem.coffeeMachine();
	private final TestItem teaPot = TestItem.teaPot();
	private CustomerTestRole customer;
	private DeliveryTestRole deliveryGuy;
	private static AspectjRecorder recorder;

	static {
		final ClassToModelGenerator generateFromCode = new ClassToModelGenerator(
				Arrays.asList(CustomerTestRole.class, DeliveryTestRole.class));
		final TestModel model = generateFromCode.generateModel();
		model.addActor(new TestActor("customer", "customer", model.getRole(CustomerTestRole.class)));
		model.addActor(new TestActor("api", "api", model.getRole(CustomerTestRole.class)));
		model.addActor(new TestActor("deliveryGuy", "deliveryGuy", model.getRole(DeliveryTestRole.class)));

		recorder = new AspectjRecorder(model);
		recorder.install();
	}

	@Before
	public void prepareApis() {

		final ExampleService testedService = new ExampleService();

		customer = new CustomerTestRole(testedService);
		deliveryGuy = new DeliveryTestRole(testedService);

		recorder.reset();
		recorder.addActor("customer", customer);
		recorder.addActor("deliveryGuy", deliveryGuy);
	}

	@After
	public void showResult() {
		final TestCase testCase = recorder.getTestCase("ATestCase");
		final HumanReadableVisitor testSummaryVisitor = new HumanReadableVisitor(testCase);
		for (final TestStep step : testCase.getSteps()) {
			System.out.println("Step " + step.getOrdinal() + ": " + testSummaryVisitor.process(step));
		}
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
		recorder.addActor("api", api);

		api.buy(inLocalShop(), coffeeMachine);
		api.checkPackage(fromShop(), teaPot);
		api.resellOwnedItem(10);

	}

}
