package ch.skymarshall.tcwriter.generators.recorder;

import static ch.skymarshall.tcwriter.generators.JsonHelper.classFile;
import static ch.skymarshall.tcwriter.generators.JsonHelper.readFile;
import static ch.skymarshall.tcwriter.generators.JsonHelper.testModelFromJson;
import static ch.skymarshall.tcwriter.generators.JsonHelper.toJson;
import static ch.skymarshall.tcwriter.generators.JsonHelper.writeFile;
import static ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory.simpleType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.skymarshall.tcwriter.generators.Helper;
import ch.skymarshall.tcwriter.generators.model.testapi.TestAction;
import ch.skymarshall.tcwriter.generators.model.testapi.TestActor;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory.ParameterNature;
import ch.skymarshall.tcwriter.generators.model.testapi.TestRole;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestReference;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.recording.ITestCaseRecorder;
import ch.skymarshall.tcwriter.recording.TestActors;
import ch.skymarshall.tcwriter.test.TestObjectDescription;

public class TestCaseRecorder implements ITestCaseRecorder {

	private static int actorIndex = 0;

	private final TestModel testModel;

	private final List<TestStep> testSteps = new ArrayList<>();
	private final Map<Object, TestParameterValue> testParameterValues = new HashMap<>();

	private final Map<Object, TestActor> actors = new HashMap<>();

	public TestCaseRecorder(final Path modelPath) throws IOException {
		this.testModel = testModelFromJson(readFile(modelPath));
	}

	public TestCaseRecorder(final TestModel model) {
		this.testModel = model;
	}

	private static void resetActorIndex() {
		actorIndex = 0;
	}

	private static int nextActorIndex() {
		return actorIndex++;
	}

	public List<TestStep> getSteps() {
		return testSteps;
	}

	public Map<Object, TestParameterValue> getTestParameterValues() {
		return testParameterValues;
	}

	public void reset() {
		resetActorIndex();
		testSteps.clear();
		actors.clear();
	}

	public TestActor recordActor(final Object actor) {
		final Class<?> roleType = actor.getClass();
		final TestActor testActor = new TestActor(nextActorIndex() + "_" + roleType.getSimpleName(),
				roleType.getSimpleName(), testModel.getRole(roleType));
		actors.put(actor, testActor);
		return testActor;
	}

	@Override
	public void recordStep(final String description, final Object recordedActor, final String apiName,
			final Object[] apiArgs) {
		final TestStep step = new TestStep(testSteps.size() + 1);

		TestActor actor = null;
		final String actorName = TestActors.getNames().get(recordedActor);
		if (actorName != null) {
			actor = testModel.getActors().get(actorName);
		}
		if (actor == null) {
			actor = actors.get(recordedActor);
		}
		if (actor == null) {
			actor = recordActor(recordedActor);
		}
		final TestRole role = actor.getRole();
		if (role == null) {
			throw new IllegalStateException("No role found for " + actor);
		}
		step.setActor(actor);

		final TestAction action = role.getActions().stream().filter(a -> matches(a, apiName, apiArgs)).findFirst()
				.orElseThrow(() -> new IllegalStateException("No action found for " + description));
		step.setAction(action);

		for (int i = 0; i < apiArgs.length; i++) {
			final Object apiArg = apiArgs[i];
			final TestParameterValue parameterValue = testParameterValues.get(apiArg);
			if (parameterValue != null) {
				step.getParametersValue().add(parameterValue.derivate(action.getParameter(i)));
			} else {
				final TestApiParameter actionParameter = step.getAction().getParameter(i);
				final TestParameterFactory def = actionParameter.asSimpleParameter();
				step.getParametersValue().add(new TestParameterValue(actionParameter, def, Objects.toString(apiArg)));
			}
		}

		testSteps.add(step);
	}

	@Override
	public void recordParamFactory(final Class<?> apiFactoryClass, final String apiName, final Object[] apiArgs,
			final Object returnValue) {
		final TestParameterFactory testParameterFactory = testModel
				.getTestParameterFactory(Helper.methodKey(apiFactoryClass, apiName));
		final TestParameterValue testParameterValue = new TestParameterValue("<PlaceHolder>", testParameterFactory);
		for (int i = 0; i < testParameterFactory.getMandatoryParameters().size(); i++) {
			testParameterValue.addComplexTypeValue(
					createFactoryParameterValue(testParameterFactory.getMandatoryParameter(i), apiArgs[i]));
		}
		testParameterValues.put(returnValue, testParameterValue);
	}

	@Override
	public void recordParamFactoryCall(final Object factory, final String callName, final Object[] args) {
		final TestParameterValue testParameterValue = testParameterValues.get(factory);
		if (testParameterValue == null) {
			// we are being called during the factory's call
			return;
		}
		final TestParameterFactory testParameterFactory = testParameterValue.getValueFactory();
		Object apiArg;
		if (args.length > 0) {
			apiArg = args[0];
		} else {
			apiArg = null;
		}
		testParameterValue.addComplexTypeValue(
				createFactoryParameterValue(testParameterFactory.getOptionalParameter(callName), apiArg));
	}

	@Override
	public void recordReturnValue(final Object reference) {
		final TestStep currentStep = testSteps.get(testSteps.size() - 1);

		final TestParameterFactory paramFactory = currentStep.asNamedReference("ref" + currentStep.getOrdinal(),
				"Value of step " + currentStep.getOrdinal());
		final TestParameterValue paramValue = new TestParameterValue("<placeHolder>", paramFactory,
				Objects.toString(reference));
		testParameterValues.put(reference, paramValue);
	}

	private TestParameterValue createFactoryParameterValue(final TestApiParameter param, final Object apiArg) {
		return new TestParameterValue(param, simpleType(param.getType()), Objects.toString(apiArg));
	}

	protected static boolean matches(final TestAction action, final String apiName, final Object[] apiArgs) {
		if (!action.getName().equals(apiName) || action.getParameters().size() != apiArgs.length) {
			return false;
		}
		for (int i = 0; i < action.getParameters().size(); i++) {
			final TestApiParameter expected = action.getParameters().get(i);
			final Object actual = apiArgs[i];
			if (!expected.getType().equals(actual.getClass().getName())
					&& !expected.getType().equals(asPrimitive(actual.getClass()).getName())) {
				return false;
			}
		}
		return true;
	}

	protected static Class<?> asPrimitive(final Class<?> clazz) {
		if (Integer.class == clazz) {
			return Integer.TYPE;
		}
		return Object.class;
	}

	public TestCase getTestCase(final String testClassName) {
		final TestCase testCase = new TestCase(testClassName, testModel);
		actors.forEach((a, ta) -> testModel.addActor(ta,
				TestActors.getDescriptions().getOrDefault(a, new TestObjectDescription(ta.getId(), ta.getId()))));
		testCase.getSteps().addAll(testSteps);
		testParameterValues.values().stream().map(TestParameterValue::getValueFactory)
				.filter(t -> t.getNature() == ParameterNature.REFERENCE)
				.forEach(t -> testCase.publishReference(((TestReference) t)));

		return testCase;
	}

	@Override
	public void save(final Path testRoot, final String testClassName) throws IOException {
		writeFile(classFile(testRoot, testClassName), toJson(getTestCase(testClassName)));
	}
}
