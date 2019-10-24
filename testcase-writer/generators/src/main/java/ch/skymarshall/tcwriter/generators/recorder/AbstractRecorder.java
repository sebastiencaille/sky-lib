package ch.skymarshall.tcwriter.generators.recorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import ch.skymarshall.tcwriter.generators.Helper;
import ch.skymarshall.tcwriter.generators.model.testapi.TestAction;
import ch.skymarshall.tcwriter.generators.model.testapi.TestActor;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterDefinition;
import ch.skymarshall.tcwriter.generators.model.testapi.TestRole;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;

public class AbstractRecorder {

	private final TestModel testModel;
	private final Map<Object, String> actorsNames = new HashMap<>();

	private final List<TestStep> testSteps = new ArrayList<>();
	private final Map<Object, TestParameterValue> testParameterValues = new HashMap<>();

	public AbstractRecorder(final TestModel model) {
		this.testModel = model;
	}

	public List<TestStep> getSteps() {
		return testSteps;
	}

	public Map<Object, TestParameterValue> getTestParameterValues() {
		return testParameterValues;
	}

	public void addActor(final String name, final Object actor) {
		actorsNames.put(actor, name);
	}

	public void reset() {
		testSteps.clear();
		actorsNames.clear();
	}

	protected void recordStep(final String description, final Object recordedActor, final String apiName,
			final Object[] apiArgs) {
		final TestStep step = new TestStep(testSteps.size() + 1);
		final TestActor actor = testModel.getActors().get(actorsNames.get(recordedActor));
		if (actor == null) {
			throw new IllegalStateException("No actor found for " + description);
		}
		final TestRole role = actor.getRole();
		if (role == null) {
			throw new IllegalStateException("No role found for " + actor);
		}
		step.setActor(actor);

		final Optional<TestAction> action = role.getActions().stream().filter(a -> matches(a, apiName, apiArgs))
				.findFirst();
		step.setAction(action.orElseThrow(() -> new IllegalStateException("No action found for " + description)));

		for (int i = 0; i < apiArgs.length; i++) {
			final Object apiArg = apiArgs[i];
			final TestParameterValue parameterValue = testParameterValues.get(apiArg);
			if (parameterValue != null) {
				step.getParametersValue().add(parameterValue);
			} else {
				final TestApiParameter actionParameter = step.getAction().getParameter(i);
				final TestParameterDefinition def = actionParameter.asSimpleParameter();
				step.getParametersValue().add(new TestParameterValue(actionParameter, def, Objects.toString(apiArg)));
			}
		}

		testSteps.add(step);
	}

	protected void recordParamFactory(final Class<?> apiFactoryClass, final String apiName, final Object[] apiArgs,
			final Object returnValue) {
		final TestParameterDefinition testParameter = testModel
				.getTestParameterFactory(Helper.methodKey(apiFactoryClass, apiName));
		final TestParameterValue testParameterValue = new TestParameterValue("Recorded", testParameter);
		for (int i = 0; i < testParameter.getMandatoryParameters().size(); i++) {
			final Object apiArg = apiArgs[i];
			final TestParameterValue mandatoryTestParameterValue = createTestParameterValue(
					testParameter.getMandatoryParameter(i), testParameterValues.get(apiArg), Objects.toString(apiArgs));
			testParameterValue.addComplexTypeValue(mandatoryTestParameterValue);
		}
		testParameterValues.put(returnValue, testParameterValue);
	}

	public TestParameterValue createTestParameterValue(final TestApiParameter param,
			final TestParameterValue factoryParameterValue, final String verbatimValue) {

		if (factoryParameterValue != null) {
			return factoryParameterValue;
		}
		return new TestParameterValue(param, null, verbatimValue);
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

	public TestCase getTestCase(final String path) {
		final TestCase testCase = new TestCase(path, testModel);
		testCase.getSteps().addAll(testSteps);
		return testCase;
	}

}
