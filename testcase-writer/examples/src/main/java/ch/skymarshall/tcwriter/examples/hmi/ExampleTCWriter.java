package ch.skymarshall.tcwriter.examples.hmi;

import static ch.skymarshall.tcwriter.generators.Helper.simpleType;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import ch.skymarshall.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.skymarshall.tcwriter.generators.GenerateModelFromCode;
import ch.skymarshall.tcwriter.generators.Helper;
import ch.skymarshall.tcwriter.generators.TestCaseToJavaGenerator;
import ch.skymarshall.tcwriter.generators.model.TestAction;
import ch.skymarshall.tcwriter.generators.model.TestActor;
import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestModel;
import ch.skymarshall.tcwriter.generators.model.TestParameter;
import ch.skymarshall.tcwriter.generators.model.TestParameterType;
import ch.skymarshall.tcwriter.generators.model.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.TestRole;
import ch.skymarshall.tcwriter.generators.model.TestStep;
import ch.skymarshall.tcwriter.hmi.TCWriter;

public class ExampleTCWriter extends TCWriter {

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

	private static TestAction find(final TestRole actor, final String name) {
		return actor.getApis().stream().filter(m -> m.getId().contains(name)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No such method: " + name));
	}

	private static TestParameter findValueFactory(final TestModel model, final String name) {
		return model.getParameterFactories().values().stream().filter(m -> m.getId().contains(name)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No parameter factory with name: " + name));
	}

	public static void main(final String[] args) {

		final TestCase tc = createTestCase();

		new ExampleTCWriter(tc).show();
	}

	public static TestCase createTestCase() {

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
		model.getDescriptions().put(deliveryGuy.getId(), "Delivery guy");
		Helper.dumpModel(model);

		final TestCase tc = new TestCase("ch.skymarshall.tcwriter.examples.MyTC", model);
		final TestParameter coffeeMachine = findValueFactory(model, "coffeeMachine");
		final TestParameter coffeeMachineOfBrand = findValueFactory(model, "coffeeMachineOfBrand");

		// Step 1
		final TestStep step1 = new TestStep();
		final TestAction action1 = find(customer.getRole(), "buy");
		final TestParameterType action1Param1 = action1.getParameter(1);
		step1.setActor(customer);
		step1.setAction(action1);
		final TestParameterValue action1Val1 = new TestParameterValue(action1Param1,
				findValueFactory(model, "inLocalShop"));
		step1.addParameter(action1Val1);
		final TestParameterValue action1Val2 = new TestParameterValue(action1Param1, coffeeMachine);
		final TestParameterType action1Param1Opt0 = coffeeMachine.getOptionalParameter(0);
		action1Val2
				.addComplexTypeValue(new TestParameterValue(action1Param1Opt0, simpleType(action1Param1Opt0), "Cheap"));
		step1.addParameter(action1Val2);
		tc.addStep(step1);

		// Step 2
		final TestStep step2 = new TestStep();
		final TestAction action2 = find(customer.getRole(), "handleAndCheckPackage");
		final TestParameterType action2Param0 = action2.getParameter(0);
		final TestParameterType action2Param1 = action2.getParameter(1);
		step2.setActor(customer);
		step2.setAction(action2);
		step2.addParameter(new TestParameterValue(action2Param0, findValueFactory(model, "fromShop")));
		final TestParameterValue action2Param1Value = new TestParameterValue(action2Param1, coffeeMachineOfBrand);
		final TestParameterType action2Param1Mand = coffeeMachineOfBrand.getMandatoryParameter(0);
		action2Param1Value.addComplexTypeValue(
				new TestParameterValue(action2Param1Mand, simpleType(action2Param1Mand), "DeLuxe"));
		step2.addParameter(action2Param1Value);
		tc.addStep(step2);

		final TestStep step3 = new TestStep();
		final TestAction action3 = find(customer.getRole(), "resellOwnedItem");
		final TestParameterType action3Param0 = action3.getParameter(0);
		step3.setActor(customer);
		step3.setAction(action3);
		step3.addParameter(new TestParameterValue(action3Param0, simpleType(action3Param0), "10"));
		tc.addStep(step3);
		return tc;
	}

}
