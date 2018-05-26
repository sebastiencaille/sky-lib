package ch.skymarshall.tcwriter.examples.hmi;

import java.util.Arrays;

import ch.skymarshall.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.skymarshall.tcwriter.generators.GenerateFromCode;
import ch.skymarshall.tcwriter.generators.Helper;
import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestMethod;
import ch.skymarshall.tcwriter.generators.model.TestModel;
import ch.skymarshall.tcwriter.generators.model.TestObject;
import ch.skymarshall.tcwriter.generators.model.TestRole;
import ch.skymarshall.tcwriter.generators.model.TestStep;
import ch.skymarshall.tcwriter.generators.model.TestValue;
import ch.skymarshall.tcwriter.hmi.TCWriter;

public class ExampleTCWriter extends TCWriter {

	private static final String A_CUSTOMER = "A Customer";

	public ExampleTCWriter(final TestCase tc) {
		super(tc);
	}

	private static TestMethod find(final TestRole actor, final String name) {
		return actor.getApis().stream().filter(m -> m.getId().contains(name)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No such method: " + name));
	}

	private static TestObject findTestObject(final TestModel model, final String name) {
		return model.getTestObjects().values().stream().filter(m -> m.getId().contains(name)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No such test object: " + name));
	}

	public static void main(final String[] args) {

		final GenerateFromCode generateFromCode = new GenerateFromCode(
				Arrays.asList(CustomerTestRole.class, DeliveryTestRole.class));
		final TestModel model = generateFromCode.generateModel();
		model.setActorRole(A_CUSTOMER, CustomerTestRole.class);
		model.setActorRole("Delivery guy", DeliveryTestRole.class);
		Helper.dumpModel(model);

		final TestCase tc = new TestCase(model);

		/**
		 * final CustomerTestActor api = new CustomerTestActor(new ExampleService());
		 *
		 * api.buy(inLocalShop(), coffeeMachine); api.handleAndCheckPackage(fromShop(),
		 * teaPot); api.resellOwnedItem();
		 */
		final TestRole testActor = model.getRoleOfActor(A_CUSTOMER);
		final TestStep step1 = new TestStep();
		step1.setActor(A_CUSTOMER);
		step1.setRole(model.getRoleOfActor(A_CUSTOMER));
		step1.setMethod(find(testActor, "buy"));
		step1.addParameter(new TestValue(findTestObject(model, "inLocalShop")));
		step1.addParameter(new TestValue(findTestObject(model, "coffeeMachine")));
		tc.addStep(step1);

		final TestStep step2 = new TestStep();
		step2.setActor(A_CUSTOMER);
		step2.setRole(model.getRoleOfActor(A_CUSTOMER));
		step2.setMethod(find(testActor, "handleAndCheckPackage"));
		step2.addParameter(new TestValue(findTestObject(model, "fromShop")));
		step2.addParameter(new TestValue(findTestObject(model, "coffeeMachine")));
		tc.addStep(step2);

		final TestStep step3 = new TestStep();
		step3.setActor(A_CUSTOMER);
		step3.setRole(model.getRoleOfActor(A_CUSTOMER));
		step3.setMethod(find(testActor, "resellOwnedItem"));
		tc.addStep(step3);

		new ExampleTCWriter(tc).show();
	}

}
