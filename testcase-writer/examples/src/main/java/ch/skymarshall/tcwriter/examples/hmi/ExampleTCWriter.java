package ch.skymarshall.tcwriter.examples.hmi;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import ch.skymarshall.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.skymarshall.tcwriter.generators.GenerateModelFromCode;
import ch.skymarshall.tcwriter.generators.Helper;
import ch.skymarshall.tcwriter.generators.TestCaseToJavaGenerator;
import ch.skymarshall.tcwriter.generators.model.TestActor;
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

	@Override
	public void generateCode(final TestCase tc) {
		try {
			new TestCaseToJavaGenerator(new File("./src/main/resources/templates/TC.template").toPath()).generate(tc,
					new File("./src/test/java").toPath());
		} catch (final IOException e) {
			e.printStackTrace();
		}

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

		final GenerateModelFromCode generateFromCode = new GenerateModelFromCode(
				Arrays.asList(CustomerTestRole.class, DeliveryTestRole.class));
		final TestModel model = generateFromCode.generateModel();
		final TestActor customer = new TestActor("customer", "customer",
				model.getRoles().get(Helper.roleKey(CustomerTestRole.class)));
		final TestActor deliveryGuy = new TestActor("delivery", "delivery",
				model.getRoles().get(Helper.roleKey(DeliveryTestRole.class)));
		model.getActors().put(customer.getId(), customer);
		model.getActors().put(deliveryGuy.getId(), deliveryGuy);
		model.getDescriptions().put(customer.getId(), "A customer");
		model.getDescriptions().put(deliveryGuy.getId(), "Delivery gux");
		Helper.dumpModel(model);

		final TestCase tc = new TestCase("ch.skymarshall.tcwriter.examples.MyTC", model);

		/**
		 * final CustomerTestActor api = new CustomerTestActor(new ExampleService());
		 *
		 * api.buy(inLocalShop(), coffeeMachine); api.handleAndCheckPackage(fromShop(),
		 * teaPot); api.resellOwnedItem();
		 */
		final TestStep step1 = new TestStep();
		final TestMethod method1 = find(customer.getRole(), "buy");
		step1.setActor(customer);
		step1.setMethod(method1);
		step1.addParameter(new TestValue(methodParamId(method1, 0), findTestObject(model, "inLocalShop")));
		step1.addParameter(new TestValue(methodParamId(method1, 1), findTestObject(model, "coffeeMachine")));
		tc.addStep(step1);

		final TestStep step2 = new TestStep();
		final TestMethod method2 = find(customer.getRole(), "handleAndCheckPackage");
		step2.setActor(customer);
		step2.setMethod(method2);
		step2.addParameter(new TestValue(methodParamId(method2, 0), findTestObject(model, "fromShop")));
		step2.addParameter(new TestValue(methodParamId(method2, 1), findTestObject(model, "coffeeMachine")));
		tc.addStep(step2);

		final TestStep step3 = new TestStep();
		step3.setActor(customer);
		step3.setMethod(find(customer.getRole(), "resellOwnedItem"));
		tc.addStep(step3);

		new ExampleTCWriter(tc).show();
	}

	private static String methodParamId(final TestMethod testMethod, final int paramIndex) {
		return testMethod.getParameters().get(paramIndex).getId();
	}

}
