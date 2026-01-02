package ch.scaille.tcwriter.services.recorder;

import static ch.scaille.tcwriter.model.dictionary.TestParameterFactory.simpleType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.scaille.tcwriter.model.TestObjectDescription;
import ch.scaille.tcwriter.model.dictionary.*;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.model.testcase.ExportableTestParameterValue;
import ch.scaille.tcwriter.model.testcase.ExportableTestStep;
import ch.scaille.tcwriter.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestReference;
import ch.scaille.tcwriter.model.testcase.TestStep;
import ch.scaille.tcwriter.persistence.IModelDao;
import ch.scaille.tcwriter.recorder.RecorderTestActors;
import ch.scaille.tcwriter.services.generators.Helper;

public class TestCaseRecorder implements ITestCaseRecorder {

    private static int actorIndex = 0;

    private final TestDictionary tcDictionary;

    private final List<TestStep> testSteps = new ArrayList<>();
    
    private final Map<Object, TestParameterValue> testParameterValues = new HashMap<>();

    private final Map<Object, TestActor> actors = new HashMap<>();

    /**
     * Creates a recorder
     */
    public TestCaseRecorder(final IModelDao modelDao, String tcDictionary) throws IOException {
        this.tcDictionary = modelDao.readTestDictionary(tcDictionary).orElseThrow(() -> new FileNotFoundException(tcDictionary));
    }

    public TestCaseRecorder(final TestDictionary tcDictionary) {
        this.tcDictionary = tcDictionary;
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

    public void reset() {
        resetActorIndex();
        testSteps.clear();
        actors.clear();
    }

    public TestActor recordActor(final Object actor) {
        final var roleType = actor.getClass();
        final var testActor = new TestActor(nextActorIndex() + "_" + roleType.getSimpleName(), roleType.getSimpleName(),
                tcDictionary.getRole(roleType));
        actors.put(actor, testActor);
        return testActor;
    }

    @Override
    public void recordStep(Object recordedActor, Method api, Object[] apiArgs) {
        final var step = new ExportableTestStep(testSteps.size() + 1);

        TestActor actor = null;
        final var actorName = RecorderTestActors.getNames().get(recordedActor);
        if (actorName != null) {
            actor = tcDictionary.getActors().get(actorName);
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

        final var action = role.getActions().stream().filter(a -> matches(a, api)).findFirst()
                .orElseThrow(() -> new IllegalStateException("No action found for " + api));
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
                step.getParametersValue().add(
                        new ExportableTestParameterValue(actionParameter, simpleParameter, Objects.toString(apiArg)));
            }
        }

        testSteps.add(step);
    }

    @Override
    public void recordParamFactory(Class<?> apiFactoryClass, Method api, Object[] apiArgs, Object returnValue) {
        final var testParameterFactory = tcDictionary
                .getTestParameterFactory(Helper.methodKey(apiFactoryClass, api.getName()));
        final var testParameterValue = new ExportableTestParameterValue("<PlaceHolder>", testParameterFactory);
        for (int i = 0; i < testParameterFactory.getMandatoryParameters().size(); i++) {
            testParameterValue.addComplexTypeValue(
                    createFactoryParameterValue(testParameterFactory.getMandatoryParameter(i), apiArgs[i]));
        }
        testParameterValues.put(returnValue, testParameterValue);
    }

    @Override
    public void recordParamFactoryCall(Object factory, Method api, Object[] args) {
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
                createFactoryParameterValue(testParameterFactory.getOptionalParameterByName(api.getName()), apiArg));
    }

    @Override
    public void recordReturnValue(final Object reference) {
        final var currentStep = testSteps.getLast();
        final var paramFactory = currentStep.asNamedReference("ref" + currentStep.getOrdinal(),
                "Value of step " + currentStep.getOrdinal());
        final var paramValue = new ExportableTestParameterValue("<placeHolder>", paramFactory,
                Objects.toString(reference));
        testParameterValues.put(reference, paramValue);
    }

    private TestParameterValue createFactoryParameterValue(final TestApiParameter param, final Object apiArg) {
        return new ExportableTestParameterValue(param, simpleType(param.getParameterType()), Objects.toString(apiArg));
    }

    protected static boolean matches(final TestAction action, Method api) {
        if (!action.getName().equals(api.getName()) || action.getParameters().size() != api.getParameterCount()) {
            return false;
        }
        for (int i = 0; i < action.getParameters().size(); i++) {
            final var expectedType = action.getParameters().get(i).getParameterType();
            final var actual = api.getParameters()[i].getType();
            try {
				if (!expectedType.equals(actual.toGenericString()) && 
					!Class.forName(expectedType).isAssignableFrom(actual)) {
				    return false;
				}
			} catch (ClassNotFoundException _) {
				return false;
			}
        }
        return true;
    }

    @Override
    public ExportableTestCase buildTestCase(final String testClassName) {
        final var testCase = new ExportableTestCase(testClassName, tcDictionary);
        testCase.getMetadata().setDescription(String.format("%s: execution at %s", testClassName,
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(LocalDateTime.now())));

        for (var actor: actors.values()) {
            var description = RecorderTestActors.getDescriptions().getOrDefault(actor.getId(), actor.getId());
            tcDictionary.addActor(actor, new TestObjectDescription(description, description));
        }
        testCase.getSteps().addAll(testSteps);
        testParameterValues.values().stream().map(TestParameterValue::getValueFactory)
                .filter(t -> t.getNature() == ParameterNature.REFERENCE)
                .forEach(t -> testCase.publishReference(((TestReference) t)));

        return testCase;
    }

}
