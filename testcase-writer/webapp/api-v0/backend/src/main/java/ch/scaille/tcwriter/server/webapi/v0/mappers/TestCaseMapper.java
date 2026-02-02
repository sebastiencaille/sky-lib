package ch.scaille.tcwriter.server.webapi.v0.mappers;

import java.util.ArrayList;
import java.util.List;

import ch.scaille.tcwriter.persistence.handlers.serdeser.Deserializers;
import ch.scaille.tcwriter.persistence.handlers.serdeser.ExportReference;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.persistence.handlers.serdeser.mixins.TestParameterValueMixin;
import ch.scaille.tcwriter.persistence.handlers.serdeser.mixins.TestReferenceMixin;
import ch.scaille.tcwriter.persistence.handlers.serdeser.mixins.TestStepMixin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.openapitools.jackson.nullable.JsonNullable;

import ch.scaille.tcwriter.generated.api.model.v0.TestCase;
import ch.scaille.tcwriter.generated.api.model.v0.TestParameterValue;
import ch.scaille.tcwriter.generated.api.model.v0.TestReference;
import ch.scaille.tcwriter.generated.api.model.v0.TestStep;
import ch.scaille.tcwriter.model.dictionary.StepClassifier;
import ch.scaille.tcwriter.model.testexec.StepStatus;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
        implementationPackage = "<PACKAGE_NAME>.generated")
@NullMarked
public interface TestCaseMapper extends MetadataMapper {

    TestCaseMapper MAPPER = Mappers.getMapper(TestCaseMapper.class);

    default JsonNullable<String> convertToDto(@Nullable StepClassifier model) {
        return model != null ? JsonNullable.of(model.name()) : JsonNullable.undefined();
    }

    default StepClassifier convertToModel(JsonNullable<Object> dto) {
        return dto.map(v -> StepClassifier.valueOf((String) v)).orElse(null);
    }

    StepStatus convertToDto(ch.scaille.tcwriter.model.testexec.StepStatus model);

    @Mapping(target = "actorRef", expression = "java(model.getActor().getId())")
    @Mapping(target = "roleRef", expression = "java(model.getRole().getId())")
    @Mapping(target = "actionRef", expression = "java(model.getAction().getId())")
    @Mapping(target = "humanReadable", ignore = true)
    TestStep convertToDto(ch.scaille.tcwriter.model.testcase.TestStep model);

    TestCase convertToDto(ch.scaille.tcwriter.model.testcase.TestCase model);

    @Mapping(target = "testParameterFactoryRef", expression = "java(model.getParameterValueFactory().getId())")
    TestParameterValue convertToDto(ch.scaille.tcwriter.model.testcase.TestParameterValue model);

    @Mapping(target = "testStepRef", expression = "java(Integer.toString(model.getStep().getOrdinal()))")
    TestReference convertToDto(ch.scaille.tcwriter.model.testcase.TestReference model);

    default ch.scaille.tcwriter.model.testcase.TestCase convertToModel(TestCase dto, TestDictionary dictionary) {
        final var exportReferences = new ArrayList<ExportReference<ch.scaille.tcwriter.model.testcase.TestCase, ?>>();
        final var testCase = convertToModelNoRef(dto, exportReferences);
        testCase.setDictionary(dictionary);
        exportReferences.forEach(ref -> ref.apply(testCase));
        return testCase;
    }

    @Mapping(target = "dictionary", ignore = true)
    @Mapping(target = "preferredDictionary", ignore = true)
    @Mapping(target = "dynamicDescriptions", ignore = true)
    @Mapping(target = "dynamicReferences", ignore = true)
    ch.scaille.tcwriter.model.testcase.TestCase convertToModelNoRef(TestCase dto,
                                                                    @Context List<ExportReference<ch.scaille.tcwriter.model.testcase.TestCase, ?>> exportReferences);

    default List<ch.scaille.tcwriter.model.testcase.TestStep> convertStepsToModel(List<TestStep> dto,
                                                                                  @Context List<ExportReference<ch.scaille.tcwriter.model.testcase.TestCase, ?>> exportReferences) {
        return dto.stream().map(step -> convertToModel(step, exportReferences)).toList();
    }

    @Mapping(target = "actor", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "action", ignore = true)
    ch.scaille.tcwriter.model.testcase.TestStep convertToModelNoRef(TestStep dto,
                                                                    @Context List<ExportReference<ch.scaille.tcwriter.model.testcase.TestCase, ?>> exportReferences);

    default ch.scaille.tcwriter.model.testcase.TestStep convertToModel(TestStep dto,
                                                                       @Context List<ExportReference<ch.scaille.tcwriter.model.testcase.TestCase, ?>> exportReferences) {
        final var testStep = convertToModelNoRef(dto, exportReferences);
        exportReferences.add(Deserializers.getTestCaseHandler(TestStep.class, TestStepMixin.ACTOR_REF).get().of(testStep, dto.getActorRef()));
        exportReferences.add(Deserializers.getTestCaseHandler(TestStep.class, TestStepMixin.ROLE_REF).get().of(testStep, dto.getRoleRef()));
        exportReferences.add(Deserializers.getTestCaseHandler(TestStep.class, TestStepMixin.ACTION_REF).get().of(testStep, dto.getActionRef()));
        return testStep;
    }

    @DoIgnore
    @Mapping(target = "step", ignore = true)
    ch.scaille.tcwriter.model.testcase.TestReference convertToModelNoRef(TestReference dto,
                                                                         @Context List<ExportReference<ch.scaille.tcwriter.model.testcase.TestCase, ?>> exportReferences);

    default ch.scaille.tcwriter.model.testcase.TestReference convertToModel(TestReference dto,
                                                                            @Context List<ExportReference<ch.scaille.tcwriter.model.testcase.TestCase, ?>> exportReferences) {
        final var reference = convertToModelNoRef(dto, exportReferences);
        exportReferences.add(Deserializers.getTestCaseHandler(TestReference.class, TestReferenceMixin.STEP_REF).get().of(reference, dto.getTestStepRef()));
        return reference;
    }

    @DoIgnore
    @Mapping(target = "derivate", ignore = true)
    @Mapping(target = "parameterValueFactory", ignore = true)
    ch.scaille.tcwriter.model.testcase.TestParameterValue convertToModelNoRef(TestParameterValue dto,
                                                                              @Context List<ExportReference<ch.scaille.tcwriter.model.testcase.TestCase, ?>> exportReferences);


    default ch.scaille.tcwriter.model.testcase.TestParameterValue convertToModel(TestParameterValue dto,
                                                                                 @Context List<ExportReference<ch.scaille.tcwriter.model.testcase.TestCase, ?>> exportReferences) {
        final var parameterValue = convertToModelNoRef(dto, exportReferences);
        exportReferences.add(Deserializers.getTestCaseHandler(TestReference.class, TestParameterValueMixin.TEST_PARAMETER_FACTORY_REF).get().of(parameterValue, dto.getTestParameterFactoryRef()));
        return parameterValue;
    }

    default List<ch.scaille.tcwriter.model.testcase.TestParameterValue> convertParameterValuesToModel(List<TestParameterValue> dto,
                                                                                                      @Context List<ExportReference<ch.scaille.tcwriter.model.testcase.TestCase, ?>> exportReferences) {
        return dto.stream().map(value -> convertToModel(value, exportReferences)).toList();
    }


}
