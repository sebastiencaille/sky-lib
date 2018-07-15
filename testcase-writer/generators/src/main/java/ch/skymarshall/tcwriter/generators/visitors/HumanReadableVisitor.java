package ch.skymarshall.tcwriter.generators.visitors;

import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterType;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestReference;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;

public class HumanReadableVisitor {

	private StringBuilder builder;
	private final TestCase tc;

	public HumanReadableVisitor(final TestCase tc) {
		this.tc = tc;
	}

	public String process(final TestStep step) {
		builder = new StringBuilder();
		builder.append("As ").append(summaryOf(step.getRole())).append(", I ").append(summaryOf(step.getAction()));
		String paramSep = ": ";
		for (int i = 0; i < step.getAction().getParameters().size(); i++) {
			final TestParameterType parameterType = step.getAction().getParameter(i);
			final boolean isNavigation = tc.getModel().isNavigation(parameterType);
			if (isNavigation) {
				paramSep = " ";
			}

			final TestParameterValue parameterValue = step.getParametersValue().get(i);
			processTestParameter(paramSep, parameterValue);
			if (isNavigation) {
				paramSep = ": ";
			} else {
				paramSep = " and ";
			}

		}
		return builder.toString();
	}

	private void processTestParameter(final String paramSep, final TestParameterValue parameterValue) {
		builder.append(paramSep).append(summaryOf(parameterValue.getTestParameter()));
		switch (parameterValue.getTestParameter().getNature()) {
		case REFERENCE:
			builder.append(" (from step ")
					.append(((TestReference) parameterValue.getTestParameter()).getStep().getOrdinal()).append(")");
			break;
		case SIMPLE_TYPE:
			builder.append(parameterValue.getSimpleValue());
			break;
		case TEST_API_TYPE:
			String sep = " ";
			for (final TestParameterType mandatoryParam : parameterValue.getTestParameter().getMandatoryParameters()) {
				processMandatoryParameter(parameterValue, sep, mandatoryParam);
				sep = ", ";
			}
			for (final TestParameterType optionalParam : parameterValue.getTestParameter().getOptionalParameters()) {
				processOptionalParameter(parameterValue, sep, optionalParam);
				sep = ", ";
			}
			break;
		default:
			break;
		}

	}

	private void processOptionalParameter(final TestParameterValue parameterValue, final String sep,
			final TestParameterType optionalParam) {
		final TestParameterValue optionalValue = parameterValue.getComplexTypeValues().get(optionalParam.getId());
		if (optionalValue != null) {
			builder.append(sep).append(summaryOf(optionalParam)).append(": ");
			processTestParameter("", optionalValue);
		}
	}

	private void processMandatoryParameter(final TestParameterValue parameterValue, final String sep,
			final TestParameterType mandatoryParam) {
		final TestParameterValue mandatoryValue = parameterValue.getComplexTypeValues().get(mandatoryParam.getId());

		final String mandatoryParamNameSummary = summaryOf(mandatoryParam);
		builder.append(sep);
		if (!mandatoryParamNameSummary.isEmpty()) {
			builder.append(mandatoryParamNameSummary).append(" ");
		}
		processTestParameter("", mandatoryValue);
	}

	private String summaryOf(final IdObject idObject) {
		return tc.descriptionOf(idObject).getStepSummary();
	}

}
