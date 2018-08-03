package ch.skymarshall.tcwriter.generators.visitors;

import java.util.List;
import java.util.stream.Collectors;

import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterType;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestReference;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;

public class HumanReadableVisitor {

	private final TestCase tc;

	public HumanReadableVisitor(final TestCase tc) {
		this.tc = tc;
	}

	public String process(final TestStep step) {
		return "As " + summaryOf(step.getRole(), null) + ", I " + summaryOf(step.getAction(),
				step.getParametersValue().stream().map(p -> processTestParameter(p)).collect(Collectors.toList()));
	}

	private String processTestParameter(final TestParameterValue parameterValue) {
		switch (parameterValue.getValueDefinition().getNature()) {
		case REFERENCE:
			return "<the value " + parameterValue.getSimpleValue() + " provided by step "
					+ ((TestReference) parameterValue.getValueDefinition()).getStep().getOrdinal() + ">";
		case SIMPLE_TYPE:
			return parameterValue.getSimpleValue();
		case TEST_API_TYPE:
			final List<String> mandatoryParams = parameterValue.getValueDefinition().getMandatoryParameters().stream()
					.map(p -> processTestParameter(parameterValue.getComplexTypeValues().get(p.getId())))
					.collect(Collectors.toList());
			final StringBuilder optionals = new StringBuilder();
			String sep = "(";
			boolean hasOptionals = false;
			for (final TestParameterType optionalParameter : parameterValue.getValueDefinition()
					.getOptionalParameters()) {
				final TestParameterValue optionalParameterValue = parameterValue.getComplexTypeValues()
						.get(optionalParameter.getId());
				if (optionalParameterValue == null) {
					continue;
				}
				optionals.append(sep).append(summaryOf(optionalParameter, null)).append(": ")
						.append(processTestParameter(optionalParameterValue));
				sep = ", ";
				hasOptionals = true;
			}
			optionals.append(")");
			return summaryOf(parameterValue.getValueDefinition(), mandatoryParams)
					+ ((hasOptionals) ? " " + optionals.toString() : "");
		default:
			return "";
		}

	}

//	private void processOptionalParameter(final TestParameterValue parameterValue, final String sep,
//			final TestParameterType optionalParam) {
//		final TestParameterValue optionalValue = parameterValue.getComplexTypeValues().get(optionalParam.getId());
//		if (optionalValue != null) {
//			builder.append(sep).append(summaryOf(optionalParam, null)).append(": ");
//			processTestParameter(optionalValue);
//		}
//	}

	private String summaryOf(final IdObject idObject, final List<String> list) {
		return String.format(tc.descriptionOf(idObject).getStepSummary(), (list != null) ? list.toArray() : null);
	}

}
