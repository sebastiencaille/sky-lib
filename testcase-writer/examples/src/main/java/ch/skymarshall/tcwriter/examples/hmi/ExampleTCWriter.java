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
		model.getDescriptions().put(deliveryGuy.getId(), "Delivery gux");
		Helper.dumpModel(model);

		final TestCase tc = new TestCase("ch.skymarshall.tcwriter.examples.MyTC", model);
		final TestParameter coffeeMachine = findValueFactory(model, "coffeeMachine");
		final TestParameter coffeeMachineOfBrand = findValueFactory(model, "coffeeMachineOfBrand");

		// Step 1
		final TestAction action1 = find(customer.getRole(), "buy");

		final TestStep step1 = new TestStep();
		step1.setActor(customer);
		step1.setMethod(action1);
		final TestParameterValue step1val1 = new TestParameterValue(actionParamIdOf(action1, 0),
				findValueFactory(model, "inLocalShop"));
		step1.addParameter(step1val1);
		final TestParameterValue step1P2Value = new TestParameterValue(actionParamIdOf(action1, 1), coffeeMachine);
		final TestParameterType step1P2Op1 = coffeeMachine.getOptionalParameters().get(0);
		step1P2Value.addComplexTypeValue(
				new TestParameterValue(step1P2Op1.getId(), simpleType(step1P2Op1)).setSimpleValue("Plouf"));
		step1.addParameter(step1P2Value);
		tc.addStep(step1);

		// Step 2
		final TestStep step2 = new TestStep();
		final TestAction method2 = find(customer.getRole(), "handleAndCheckPackage");
		step2.setActor(customer);
		step2.setMethod(method2);
		step2.addParameter(new TestParameterValue(actionParamIdOf(method2, 0), findValueFactory(model, "fromShop")));
		final TestParameterValue step2P2Value = new TestParameterValue(actionParamIdOf(method2, 1),
				coffeeMachineOfBrand);
		final TestParameterType step2P2P1 = coffeeMachineOfBrand.getMandatoryParameters().get(0);
		step2P2Value.addComplexTypeValue(
				new TestParameterValue(step2P2P1.getId(), simpleType(step2P2P1)).setSimpleValue("Blux"));
		step2.addParameter(step2P2Value);
		tc.addStep(step2);

		final TestStep step3 = new TestStep();
		step3.setActor(customer);
		step3.setMethod(find(customer.getRole(), "resellOwnedItem"));
		tc.addStep(step3);
		return tc;
	}

	private static String actionParamIdOf(final TestAction testMethod, final int paramIndex) {
		return testMethod.getParameters().get(paramIndex).getId();
	}

}
