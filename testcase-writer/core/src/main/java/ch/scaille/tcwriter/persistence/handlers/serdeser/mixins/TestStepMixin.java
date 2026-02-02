package ch.scaille.tcwriter.persistence.handlers.serdeser.mixins;

import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestStep;
import ch.scaille.tcwriter.persistence.handlers.serdeser.ExportReference;
import ch.scaille.tcwriter.persistence.handlers.serdeser.ExportReferenceWriter;
import ch.scaille.tcwriter.persistence.handlers.serdeser.ReferenceHandler;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.annotation.JsonAppend;

import java.util.Objects;

@JsonIgnoreProperties({"actor", "role", "action"})
@JsonAppend(props = {
        @JsonAppend.Prop(value = ExportReferenceWriter.class, name = TestStepMixin.ROLE_REF, type = ExportReference.class),
        @JsonAppend.Prop(value = ExportReferenceWriter.class, name = TestStepMixin.ACTOR_REF, type = ExportReference.class),
        @JsonAppend.Prop(value = ExportReferenceWriter.class, name = TestStepMixin.ACTION_REF, type = ExportReference.class)
})
public class TestStepMixin {
    private TestStepMixin() {
        /* This utility class should not be instantiated */
    }


    public static final String ROLE_REF = "roleRef";
    public static final ReferenceHandler<TestCase, TestStep> ROLE_REF_HANDLER =
            new ReferenceHandler<>(TestStep.class, ROLE_REF,
                    testStep -> testStep.getRole().getId(),
                    (testCase, testStep, reference) -> testStep.setRole(Objects.requireNonNull(testCase.getDictionary().getRoles().get(reference), reference)));

    public static final String ACTOR_REF = "actorRef";
    public static final ReferenceHandler<TestCase, TestStep> ACTOR_REF_HANDLER =
            new ReferenceHandler<>(TestStep.class, ACTOR_REF,
                    testStep -> testStep.getActor().getId(),
                    (testCase, testStep, reference) -> testStep.setActor(Objects.requireNonNull(testCase.getDictionary().getActors().get(reference), reference)));

    public static final String ACTION_REF = "actionRef";
    public static final ReferenceHandler<TestCase, TestStep> ACTION_REF_HANDLER =
            new ReferenceHandler<>(TestStep.class, ACTION_REF,
                    testStep -> testStep.getAction().getId(),
                    (testCase, testStep, reference) -> testStep.setAction(Objects.requireNonNull(testStep.getRole().getAction(reference), reference)));
}
