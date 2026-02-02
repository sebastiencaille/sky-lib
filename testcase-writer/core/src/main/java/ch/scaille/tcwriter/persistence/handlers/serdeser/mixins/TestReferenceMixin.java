package ch.scaille.tcwriter.persistence.handlers.serdeser.mixins;

import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestReference;
import ch.scaille.tcwriter.persistence.handlers.serdeser.ExportReference;
import ch.scaille.tcwriter.persistence.handlers.serdeser.ExportReferenceWriter;
import ch.scaille.tcwriter.persistence.handlers.serdeser.ReferenceHandler;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.annotation.JsonAppend;

import static ch.scaille.tcwriter.persistence.handlers.serdeser.mixins.TestReferenceMixin.STEP_REF;

@JsonIgnoreProperties({ "step", "mandatoryParameters", "optionalParameters" })
@JsonAppend(props = {
        @JsonAppend.Prop(value = ExportReferenceWriter.class, name = STEP_REF, type = ExportReference.class)
})
public class TestReferenceMixin {

    public static final String STEP_REF = "stepRef";
    public static final ReferenceHandler<TestCase, TestReference> REF_HANDLER =
            new ReferenceHandler<>(TestReference.class, STEP_REF,
                    testRef -> Integer.toString(testRef.getStep().getOrdinal()),
                    (testCase, testRef, reference) -> testRef.setStep(testCase.getSteps().get(Integer.parseInt(reference) - 1)));

    private TestReferenceMixin() {
        /* This utility class should not be instantiated */
    }

}
