package ch.scaille.tcwriter.server.webapi.v0.mappers;

import java.util.ArrayList;
import java.util.List;

import ch.scaille.tcwriter.model.ExportReference;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.openapitools.jackson.nullable.JsonNullable;

import ch.scaille.tcwriter.generated.api.model.v0.TestCase;
import ch.scaille.tcwriter.generated.api.model.v0.TestParameterValue;
import ch.scaille.tcwriter.generated.api.model.v0.TestReference;
import ch.scaille.tcwriter.generated.api.model.v0.TestStep;
import ch.scaille.tcwriter.model.dictionary.StepClassifier;
import ch.scaille.tcwriter.model.testcase.ExportableTestParameterValue;
import ch.scaille.tcwriter.model.testcase.ExportableTestStep;
import ch.scaille.tcwriter.model.testexec.StepStatus;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
        implementationPackage = "<PACKAGE_NAME>.generated")
@NullMarked
public interface TestCaseMapper extends MetadataMapper {

    TestCaseMapper MAPPER = Mappers.getMapper(TestCaseMapper.class);

    default String convertToDto(ch.scaille.tcwriter.model.ExportReference model) {
        return model.getId();
    }

    default ExportReference convertToExport(String referenceId, @Context List<ExportReference> exportReferences) {
        final var exportReference = new ExportReference(referenceId);
        exportReferences.add(exportReference);
        return exportReference;
    }

    default JsonNullable<String> convertToDto(@Nullable StepClassifier model) {
        return model != null ? JsonNullable.of(model.name()) : JsonNullable.undefined();
    }

    default StepClassifier convertToModel(JsonNullable<Object> dto) {
        return dto.map(v -> StepClassifier.valueOf((String) v)).orElse(null);
    }

    TestCase convertToDto(ch.scaille.tcwriter.model.testcase.ExportableTestCase model);

    @Mapping(target = "dictionary", ignore = true)
    @Mapping(target = "testDictionary", ignore = true)
    @Mapping(target = "exportedReferences", ignore = true)
    @Mapping(target = "preferredDictionary", ignore = true)
    @Mapping(target = "dynamicDescriptions", ignore = true)
    @Mapping(target = "dynamicReferences", ignore = true)
    ch.scaille.tcwriter.model.testcase.ExportableTestCase convertToExportableNoReconciliate(TestCase dto, @Context List<ExportReference> exportReferences);

    default ch.scaille.tcwriter.model.testcase.ExportableTestCase convertToExportable(TestCase dto, TestDictionary dictionary) {
        final var exportReferences = new ArrayList<ExportReference>();
        final var exportableTest = convertToExportableNoReconciliate(dto, exportReferences);
        exportableTest.setDictionary(dictionary);
        exportableTest.setExportedReferences(exportReferences);
        exportableTest.restoreReferences();
        return exportableTest;
    }

    default List<TestStep> convertToDtoSteps(List<ch.scaille.tcwriter.model.testcase.TestStep> model) {
        return model.stream().map(ExportableTestStep.class::cast).map(MAPPER::convert).toList();
    }

    List<ch.scaille.tcwriter.model.testcase.ExportableTestStep> convertToExportableSteps(List<TestStep> model, @Context List<ExportReference> exportReferences);

    @Mapping(target = "humanReadable", ignore = true)
    TestStep convert(ch.scaille.tcwriter.model.testcase.ExportableTestStep model);

    @Mapping(target = "actor", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "action", ignore = true)
    ch.scaille.tcwriter.model.testcase.ExportableTestStep convertToExportable(TestStep model, @Context List<ExportReference> exportReferences);

    TestParameterValue convertToDto(ch.scaille.tcwriter.model.testcase.ExportableTestParameterValue model);

    @Mapping(target = "testParameterFactoryRef", expression = "java(convertToDto(((ch.scaille.tcwriter.model.testcase.ExportableTestParameterValue) model).getTestParameterFactoryRef()))")
    TestParameterValue convertToDto(ch.scaille.tcwriter.model.testcase.TestParameterValue model);

    @Mapping(target = "derivate", ignore = true)
    @Mapping(target = "parameterFactory", ignore = true)
    @Mapping(target = "factory", ignore = true)
    @Mapping(target = "valueFactory", ignore = true)
    ch.scaille.tcwriter.model.testcase.ExportableTestParameterValue convertToExportable(TestParameterValue dto, @Context List<ExportReference> exportReferences);

    default List<TestParameterValue> convertValuesToDto(List<ch.scaille.tcwriter.model.testcase.TestParameterValue> model) {
        return model.stream().map(ExportableTestParameterValue.class::cast).map(this::convertToDto).toList();
    }

    default List<ch.scaille.tcwriter.model.testcase.TestParameterValue> convertValuesToModel(
            List<TestParameterValue> dto, @Context List<ExportReference> exportReferences) {
        return dto.stream().map(v -> (ch.scaille.tcwriter.model.testcase.TestParameterValue) this.convertToExportable(v, exportReferences)).toList();
    }

    List<ch.scaille.tcwriter.model.testcase.ExportableTestParameterValue> convertToExportableValues(List<TestParameterValue> dto, @Context List<ExportReference> exportReferences);

    @Mapping(target = "testStepRef", expression = "java(convertToDto(((ch.scaille.tcwriter.model.testcase.ExportableTestReference) model).getTestStepRef()))")
    TestReference convertToDto(ch.scaille.tcwriter.model.testcase.TestReference model);

    @Mapping(target = "step", ignore = true)
    ch.scaille.tcwriter.model.testcase.ExportableTestReference convertToExportable(TestReference dto, @Context List<ExportReference> exportReferences);

    StepStatus convertToDto(ch.scaille.tcwriter.model.testexec.StepStatus model);

}
