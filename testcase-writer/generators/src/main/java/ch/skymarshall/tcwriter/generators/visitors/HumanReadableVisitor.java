package ch.skymarshall.tcwriter.generators.visitors;

import java.util.List;
import java.util.stream.Collectors;

import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestReference;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.test.TestObjectDescription;

public class HumanReadableVisitor {

	private final TestCase tc;

	public HumanReadableVisitor(final TestCase tc) {
		this.tc = tc;
	}

	public String process(final TestStep step) {
		String actorSummary = summaryOf(step.getActor(), null);
		if (actorSummary == null) {
			actorSummary = summaryOf(step.getRole(), null);
		}
		return "As " + actorSummary + ", I " + summaryOf(step.getAction(),
				step.getParametersValue().stream().map(this::processTestParameter).collect(Collectors.toList()));
	}

	private String processTestParameter(final TestParameterValue parameterValue) {
		switch (parameterValue.getValueFactory().getNature()) {
		case REFERENCE:
			return "<the value " + parameterValue.getSimpleValue() + " provided by step "
					+ ((TestReference) parameterValue.getValueFactory()).getStep().getOrdinal() + ">";
		case SIMPLE_TYPE:
			final String type = parameterValue.getValueFactory().getType();
			if (Boolean.class.getName().equals(type) || Boolean.TYPE.getName().equals(type)) {
				return Boolean.TRUE.toString().equals(parameterValue.getSimpleValue()) ? "yes" : "no";
			}
			return parameterValue.getSimpleValue();
		case TEST_API:
			final List<String> mandatoryParams = parameterValue.getValueFactory().getMandatoryParameters().stream()
					.map(p -> processTestParameter(parameterValue.getComplexTypeValues().get(p.getId())))
					.collect(Collectors.toList());
			final StringBuilder optionals = new StringBuilder();
			String sep = "(";
			boolean hasOptionals = false;
			for (final TestApiParameter optionalParameter : parameterValue.getValueFactory().getOptionalParameters()) {
				final TestParameterValue optionalParameterValue = parameterValue.getComplexTypeValues()
						.get(optionalParameter.getId());
				if (optionalParameterValue == null) {
					continue;
				}
				optionals.append(sep).append(summaryOf(optionalParameter, null)).append(": ");
				if (optionalParameter.hasType()) {
					optionals.append(processTestParameter(optionalParameterValue));
				} else {
					optionals.append("yes");
				}
				sep = ", ";
				hasOptionals = true;
			}
			optionals.append(")");
			return summaryOf(parameterValue.getValueFactory(), mandatoryParams)
					+ ((hasOptionals) ? " " + optionals.toString() : "");
		default:
			return "";
		}

	}

	private String summaryOf(final IdObject idObject, final List<String> list) {
		final TestObjectDescription description = tc.descriptionOf(idObject);
		if (description == null) {
			return null;
		}
		return String.format(description.getStepSummary(), (list != null) ? list.toArray() : null);
	}

}
