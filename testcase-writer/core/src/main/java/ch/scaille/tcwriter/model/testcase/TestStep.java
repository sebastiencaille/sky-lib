package ch.scaille.tcwriter.model.testcase;

import java.util.ArrayList;
import java.util.List;

import ch.scaille.tcwriter.mappers.Default;
import ch.scaille.tcwriter.model.dictionary.StepClassifier;
import ch.scaille.tcwriter.model.dictionary.TestAction;
import ch.scaille.tcwriter.model.dictionary.TestActor;
import ch.scaille.tcwriter.model.dictionary.TestRole;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@NullMarked
public class TestStep {

    private int ordinal;
    protected TestActor actor = TestActor.NOT_SET;
    protected TestRole role = TestRole.NOT_SET;
    protected TestAction action = TestAction.NOT_SET;
    private final List<TestParameterValue> parametersValue = new ArrayList<>();
    @Nullable
    private TestReference reference;
    @Nullable
    private StepClassifier classifier = null;

    public TestStep(final int ordinal) {
        this.ordinal = ordinal;
    }

    @Default
    public TestStep(final int ordinal, TestActor actor, TestRole role, TestAction action, List<TestParameterValue> parametersValue, TestReference reference, StepClassifier classifier) {
        this.ordinal = ordinal;
        this.actor = actor;
        this.role = role;
        this.action = action;
        this.parametersValue.addAll(parametersValue);
        this.reference = reference;
        this.classifier = classifier;
    }

    public void setActor(final TestActor actor) {
        this.actor = actor;
        this.role = actor.getRole();
    }

    public void addParameter(final TestParameterValue parameterValue) {
        this.parametersValue.add(parameterValue);
    }

    public TestReference asNamedReference(final String namedReference, final String description) {
        if (reference == null) {
            reference = createTestReference(this, namedReference, description);
        }
        return reference;
    }

    public TestParameterValue getParametersValue(final int index) {
        return parametersValue.get(index);
    }

    public TestStep duplicate() {
        final var newTestStep = createTestStep();
        newTestStep.setActor(actor);
        newTestStep.setAction(action);
        parametersValue.forEach(p -> newTestStep.addParameter(p.duplicate()));
        return newTestStep;
    }

    protected TestStep createTestStep() {
        return new TestStep(-1);
    }

    protected TestReference createTestReference(TestStep testStep, String namedReference, String description) {
        return new TestReference(testStep, namedReference, description);
    }


    @Override
    public String toString() {
        return actor.getName() + "." + action.getName();
    }

    public void fixClassifier() {
        if (classifier != null && List.of(action.getAllowedClassifiers()).contains(classifier)) {
            return;
        }
        setClassifier(action.getAllowedClassifiers()[0]);
    }

}
