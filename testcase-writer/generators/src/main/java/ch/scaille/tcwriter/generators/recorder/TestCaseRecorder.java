package ch.scaille.tcwriter.generators.recorder;

import static ch.scaille.tcwriter.generators.model.testapi.TestParameterFactory.simpleType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.scaille.tcwriter.generators.Helper;
import ch.scaille.tcwriter.generators.model.persistence.FsModelDao;
import ch.scaille.tcwriter.generators.model.persistence.IModelDao;
import ch.scaille.tcwriter.generators.model.testapi.TestAction;
import ch.scaille.tcwriter.generators.model.testapi.TestActor;
import ch.scaille.tcwriter.generators.model.testapi.TestApiParameter;
import ch.scaille.tcwriter.generators.model.testapi.TestDictionary;
import ch.scaille.tcwriter.generators.model.testapi.TestParameterFactory.ParameterNature;
import ch.scaille.tcwriter.generators.model.testcase.TestCase;
import ch.scaille.tcwriter.generators.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.generators.model.testcase.TestReference;
import ch.scaille.tcwriter.generators.model.testcase.TestStep;
import ch.scaille.tcwriter.recording.ITestCaseRecorder;
import ch.scaille.tcwriter.recording.RecorderTestActors;
import ch.scaille.tcwriter.tc.TestObjectDescription;

public class TestCaseRecorder implements ITestCaseRecorder {

	private static int actorIndex = 0;

	private final TestDictionary testDictionary;

	private final List<TestStep> testSteps = new ArrayList<>();
	private final Map<Object, TestParameterValue> testParameterValues = new HashMap<>();

	private final Map<Object, TestActor> actors = new HashMap<>();

	private final IModelDao modelDao;

	public TestCaseRecorder(final IModelDao modelDao) throws IOException {
		this.modelDao = modelDao;
		this.testDictionary = modelDao.readTestDictionary();
	}

	public TestCaseRecorder(final IModelDao modelDao, final TestDictionary model) {
		this.modelDao = modelDao;
		this.testDictionary = model;
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
		final var roleType = actor.getClass();
		final var testActor = new TestActor(nextActorIndex() + "_" + roleType.getSimpleName(), roleType.getSimpleName(),
				testDictionary.getRole(roleType));
		actors.put(actor, testActor);
		return testActor;
	}

	@Override
	public void recordStep(final String description, final Object recordedActor, final String apiName,
			final Object[] apiArgs) {
		final var step = new TestStep(testSteps.size() + 1);

		TestActor actor = null;
		final var actorName = RecorderTestActors.getNames().get(recordedActor);
		if (actorName != null) {
			actor = testDictionary.getActors().get(actorName);
		}
		if (actor == null) {
			actor = actors.get(recordedActor);
		}
		if (actor == null) {
			actor = recordActor(recordedActor);
		}
		final var role = actor.getRole();
		if (role == null) {
			throw new IllegalStateException("No role found for " + actor);
		}
		step.setActor(actor);

		final var action = role.getActions().stream().filter(a -> matches(a, apiName, apiArgs)).findFirst()
				.orElseThrow(() -> new IllegalStateException("No action found for " + description));
		step.setAction(action);
		step.fixClassifier();

		for (int i = 0; i < apiArgs.length; i++) {
			final var apiArg = apiArgs[i];
			final var parameterValue = testParameterValues.get(apiArg);
			if (parameterValue != null) {
				step.getParametersValue().add(parameterValue.derivate(action.getParameter(i)));
			} else {
				final var actionParameter = step.getAction().getParameter(i);
				final var simpleParameter = actionParameter.asSimpleParameter();
				step.getParametersValue()
						.add(new TestParameterValue(actionParameter, simpleParameter, Objects.toString(apiArg)));
			}
		}

		testSteps.add(step);
	}

	@Override
	public void recordParamFactory(final Class<?> apiFactoryClass, final String apiName, final Object[] apiArgs,
			final Object returnValue) {
		final var testParameterFactory = testDictionary
				.getTestParameterFactory(Helper.methodKey(apiFactoryClass, apiName));
		final var testParameterValue = new TestParameterValue("<PlaceHolder>", testParameterFactory);
		for (int i = 0; i < testParameterFactory.getMandatoryParameters().size(); i++) {
			testParameterValue.addComplexTypeValue(
					createFactoryParameterValue(testParameterFactory.getMandatoryParameter(i), apiArgs[i]));
		}
		testParameterValues.put(returnValue, testParameterValue);
	}

	@Override
	public void recordParamFactoryCall(final Object factory, final String callName, final Object[] args) {
		final var testParameterValue = testParameterValues.get(factory);
		if (testParameterValue == null) {
			// we are being called during the factory's call
			return;
		}
		final var testParameterFactory = testParameterValue.getValueFactory();
		Object apiArg;
		if (args.length > 0) {
			apiArg = args[0];
		} else {
			apiArg = null;
		}
		testParameterValue.addComplexTypeValue(
				createFactoryParameterValue(testParameterFactory.getOptionalParameterByName(callName), apiArg));
	}

	@Override
	public void recordReturnValue(final Object reference) {
		final var currentStep = testSteps.get(testSteps.size() - 1);

		final var paramFactory = currentStep.asNamedReference("ref" + currentStep.getOrdinal(),
				"Value of step " + currentStep.getOrdinal());
		final var paramValue = new TestParameterValue("<placeHolder>", paramFactory, Objects.toString(reference));
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
			final var expected = action.getParameters().get(i);
			final var actual = apiArgs[i];
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
		final var testCase = new TestCase(testClassName, testDictionary);
		actors.forEach((a, ta) -> testDictionary.addActor(ta, RecorderTestActors.getDescriptions().getOrDefault(a,
				new TestObjectDescription(ta.getId(), ta.getId()))));
		testCase.getSteps().addAll(testSteps);
		testParameterValues.values().stream().map(TestParameterValue::getValueFactory)
				.filter(t -> t.getNature() == ParameterNature.REFERENCE)
				.forEach(t -> testCase.publishReference(((TestReference) t)));

		return testCase;
	}

	@Override
	public void save(final Path testRoot, final String testClassName) throws IOException {
		modelDao.writeTestCase(FsModelDao.classFile(testRoot, testClassName).toString(), getTestCase(testClassName));
	}
}
