package ch.scaille.tcwriter.server.webapi.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.openapitools.jackson.nullable.JsonNullable;

import ch.scaille.tcwriter.generated.api.model.TestCase;
import ch.scaille.tcwriter.generated.api.model.TestParameterValue;
import ch.scaille.tcwriter.generated.api.model.TestReference;
import ch.scaille.tcwriter.generated.api.model.TestStep;
import ch.scaille.tcwriter.model.dictionary.StepClassifier;
import ch.scaille.tcwriter.model.testcase.ExportableTestParameterValue;
import ch.scaille.tcwriter.model.testcase.ExportableTestReference;
import ch.scaille.tcwriter.model.testcase.ExportableTestStep;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TestCaseMapper {

	TestCaseMapper MAPPER = Mappers.getMapper(TestCaseMapper.class);

	default String convert(ch.scaille.tcwriter.model.ExportReference model) {
		return model.getId();
	}

	default JsonNullable<Object> convert(StepClassifier model) {
		return JsonNullable.of(model.name());
	}

	TestCase convert(ch.scaille.tcwriter.model.testcase.ExportableTestCase model);

	default List<TestStep> convertExportableSteps(List<ch.scaille.tcwriter.model.testcase.TestStep> model) {
		return model.stream().map(ExportableTestStep.class::cast).map(MAPPER::convert).collect(Collectors.toList());
	}

	TestStep convert(ch.scaille.tcwriter.model.testcase.ExportableTestStep model);

	default List<TestParameterValue> convertExportableTestParameterValue(
			List<ch.scaille.tcwriter.model.testcase.TestParameterValue> model) {
		return model.stream().map(ExportableTestParameterValue.class::cast).map(MAPPER::convert)
				.collect(Collectors.toList());
	}

	default TestParameterValue convert(ch.scaille.tcwriter.model.testcase.TestParameterValue model) {
		return MAPPER.convertExportable((ExportableTestParameterValue)model);
	}

	TestParameterValue convertExportable(ch.scaille.tcwriter.model.testcase.ExportableTestParameterValue model);

	default TestReference convert(ch.scaille.tcwriter.model.testcase.TestReference model) {
		return MAPPER.convertExportable((ExportableTestReference) model);
	}

	TestReference convertExportable(ch.scaille.tcwriter.model.testcase.ExportableTestReference model);
}