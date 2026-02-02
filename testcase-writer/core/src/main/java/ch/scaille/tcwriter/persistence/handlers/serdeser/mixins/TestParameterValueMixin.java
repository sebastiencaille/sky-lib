package ch.scaille.tcwriter.persistence.handlers.serdeser.mixins;

import ch.scaille.tcwriter.model.dictionary.TestParameterFactory;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.persistence.handlers.serdeser.ExportReference;
import ch.scaille.tcwriter.persistence.handlers.serdeser.ExportReferenceWriter;
import ch.scaille.tcwriter.persistence.handlers.serdeser.ReferenceHandler;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.annotation.JsonAppend;

import static ch.scaille.tcwriter.persistence.handlers.serdeser.mixins.TestParameterValueMixin.TEST_PARAMETER_FACTORY_REF;

@JsonIgnoreProperties("parameterValueFactory")
@JsonAppend(props = {
        @JsonAppend.Prop(value = ExportReferenceWriter.class, name = TEST_PARAMETER_FACTORY_REF, type = ExportReference.class)
})
@NullMarked
public class TestParameterValueMixin {
    private TestParameterValueMixin() {
        /* This utility class should not be instantiated */
    }


    public static final String TEST_PARAMETER_FACTORY_REF = "testParameterFactoryRef";
    public static final ReferenceHandler<TestCase, TestParameterValue> REF_HANDLER =
            new ReferenceHandler<>(TestParameterValue.class, TEST_PARAMETER_FACTORY_REF,
                    paramValue -> paramValue.getParameterValueFactory().getId(),
                    (testCase, paramValue, reference) ->
                            paramValue.setParameterValueFactory(findValueFactory(testCase, reference)));

    private static TestParameterFactory findValueFactory(TestCase testCase, String reference) {
        return testCase.getDictionary().getTestParameterFactory(reference)
                .or(() -> testCase.findReferenceInSteps(reference))
                .orElseGet(() -> TestParameterFactory.simpleType(reference));
    }
}
