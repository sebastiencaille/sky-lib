package ch.scaille.tcwriter.model.testcase;

import ch.scaille.tcwriter.mappers.Default;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.scaille.tcwriter.model.ExportReference;
import lombok.Getter;

@JsonIgnoreProperties({"step"})
@Getter
public class ExportableTestReference extends TestReference {

    public ExportableTestReference() {
        this(ExportableTestStep.EMPTY_STEP, null, null);
    }

    @Default
    @JsonCreator
    public ExportableTestReference(final String id, final String name,
                                   String parameterType,
                                   String description) {
        super(id, name,  parameterType, description);
    }

    protected ExportableTestReference(TestStep step, String name, String description) {
        super(step, name, description);
    }

    @JsonProperty
    public ExportReference getTestStepRef() {
        return new ExportReference(Integer.toString(step.getOrdinal()));
    }

    public void setTestStepRef(final ExportReference ref) {
        ref.setRestoreAction((testCase, id) -> step = testCase.getSteps().get(Integer.parseInt(id) - 1));
    }


}
