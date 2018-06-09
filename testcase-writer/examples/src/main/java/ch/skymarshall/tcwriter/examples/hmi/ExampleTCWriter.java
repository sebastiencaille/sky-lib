package ch.skymarshall.tcwriter.examples.hmi;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import ch.skymarshall.tcwriter.examples.api.interfaces.CustomerTestRole;
import ch.skymarshall.tcwriter.examples.api.interfaces.DeliveryTestRole;
import ch.skymarshall.tcwriter.generators.GenerateModelFromCode;
import ch.skymarshall.tcwriter.generators.Helper;
import ch.skymarshall.tcwriter.generators.TestCaseToJavaGenerator;
import ch.skymarshall.tcwriter.generators.model.ObjectDescription;
import ch.skymarshall.tcwriter.generators.model.TestAction;
import ch.skymarshall.tcwriter.generators.model.TestActor;
import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.TestModel;
import ch.skymarshall.tcwriter.generators.model.TestParameter;
import ch.skymarshall.tcwriter.generators.model.TestParameterType;
import ch.skymarshall.tcwriter.generators.model.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.TestRole;
import ch.skymarshall.tcwriter.generators.model.TestStep;
import ch.skymarshall.tcwriter.hmi.TCWriter;

public class ExampleTCWriter extends TCWriter {

	private static final String REF_ANOTHER_BRAND = "anotherBrand";

	public ExampleTCWriter(final TestCase tc) {
		super(tc);
	}

	@Override
	public File generateCode(final TestCase tc) throws TestCaseException, IOException {
		return new TestCaseToJavaGenerator(new File("./src/main/resources/templates/TC.template").toPath()).generate(tc,
				new File("./src/test/java").toPath());
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
		model.addDescription(customer, new ObjectDescription("A customer", "a customer"));
		model.addDescription(deliveryGuy, new ObjectDescription("Delivery guy", "a delivery guy"));
		Helper.dumpModel(model);

		final TestCase tc = new TestCase("ch.skymarshall.tcwriter.examples.MyTC", model);
		final TestParameter coffeeMachine = findValueFactory(model, "coffeeMachine");
		final TestParameter coffeeMachineOfBrand = findValueFactory(model, "coffeeMachineOfBrand");

		int stepIndex = 1;

		//
		final TestStep step1 = new TestStep(stepIndex++);
		final TestAction action1 = find(customer.getRole(), "buy");
		final TestParameterType action1Param1 = action1.getParameter(1);
		step1.setActor(customer);
		step1.setAction(action1);
		final TestParameterValue action1Val1 = new TestParameterValue(action1Param1,
				findValueFactory(model, "inLocalShop"));
		step1.addParameter(action1Val1);
		final TestParameterValue action1Val2 = new TestParameterValue(action1Param1, coffeeMachine);
		final TestParameterType action1Param1Opt0 = coffeeMachine.getOptionalParameter(0);
		action1Val2.addComplexTypeValue(
				new TestParameterValue(action1Param1Opt0, action1Param1Opt0.asParameter(), "DeLuxeBrand"));
		step1.addParameter(action1Val2);
		tc.addStep(step1);

		//
		final TestStep step2 = new TestStep(stepIndex++);
		final TestAction action2 = find(customer.getRole(), "checkPackage");
		final TestParameterType action2Param0 = action2.getParameter(0);
		final TestParameterType action2Param1 = action2.getParameter(1);
		step2.setActor(customer);
		step2.setAction(action2);
		step2.addParameter(new TestParameterValue(action2Param0, findValueFactory(model, "fromShop")));
		final TestParameterValue action2Param1Value = new TestParameterValue(action2Param1, coffeeMachineOfBrand);
		final TestParameterType action2Param1Mand = coffeeMachineOfBrand.getMandatoryParameter(0);
		action2Param1Value.addComplexTypeValue(
				new TestParameterValue(action2Param1Mand, action2Param1Mand.asParameter(), "DeLuxeBrand"));
		step2.addParameter(action2Param1Value);
		tc.addStep(step2);

		// Step 3
		final TestStep step3 = new TestStep(stepIndex++);
		final TestAction action3 = find(customer.getRole(), "resellOwnedItem");
		final TestParameterType action3Param0 = action3.getParameter(0);
		step3.setActor(customer);
		step3.setAction(action3);
		step3.addParameter(new TestParameterValue(action3Param0, action3Param0.asParameter(), "10"));
		tc.addStep(step3);

		// Step 4
		final TestStep step4 = new TestStep(stepIndex++);
		final TestAction action4 = find(customer.getRole(), "findAnotherBrand");
		step4.setActor(customer);
		step4.setAction(action4);
		tc.publishReference(step4.asNamedReference(REF_ANOTHER_BRAND, "another brand"));
		tc.addStep(step4);

		// Step 5
		final TestStep step5 = new TestStep(stepIndex++);
		final TestAction action5 = find(customer.getRole(), "keepNote");
		step5.setActor(customer);
		step5.setAction(action5);
		final TestParameterType action5param0 = action5.getParameter(0);
		step5.addParameter(
				new TestParameterValue(action5param0, tc.getReference(REF_ANOTHER_BRAND), REF_ANOTHER_BRAND));
		tc.addStep(step5);

		return tc;
	}

}
