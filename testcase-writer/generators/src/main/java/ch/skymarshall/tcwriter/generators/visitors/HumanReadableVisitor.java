package ch.skymarshall.tcwriter.generators.visitors;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.VisibleForTesting;

import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestReference;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.tcwriter.tc.TestObjectDescription;

public class HumanReadableVisitor {

	private final TestCase tc;
	private final boolean withStepNumbers;

	public HumanReadableVisitor(final TestCase tc, final boolean withStepNumbers) {
		this.tc = tc;
		this.withStepNumbers = withStepNumbers;
	}

	public String process(final TestStep step) {
		String actorSummary = summaryOf(step.getActor(), null);
		if (actorSummary == null) {
			actorSummary = summaryOf(step.getRole(), null);
		}
		final StringBuilder result = new StringBuilder();
		if (withStepNumbers) {
			result.append(step.getOrdinal()).append(". ");
		}
		result.append("As ").append(actorSummary).append(", ").append(summaryOf(step.getAction(),
				step.getParametersValue().stream().map(this::processTestParameter).collect(toList())));

		return result.toString();
	}

	private String processTestParameter(final TestParameterValue parameterValue) {
		switch (parameterValue.getValueFactory().getNature()) {
		case REFERENCE:
			final TestReference ref = (TestReference) parameterValue.getValueFactory();
			return "[" + ref.toDescription().getHumanReadable() + ": " + parameterValue.getSimpleValue() + "]";
		case SIMPLE_TYPE:
			final String type = parameterValue.getValueFactory().getType();
			if (Boolean.class.getName().equals(type) || Boolean.TYPE.getName().equals(type)) {
				return Boolean.TRUE.toString().equals(parameterValue.getSimpleValue()) ? "yes" : "no";
			}
			return parameterValue.getSimpleValue();
		case TEST_API:
			final List<String> mandatoryParams = parameterValue.getValueFactory().getMandatoryParameters().stream()
					.map(p -> processTestParameter(parameterValue.getComplexTypeValues().get(p.getId())))
					.collect(toList());
			return processTestParameter(parameterValue, mandatoryParams);
		default:
			return "";
		}

	}

	private String processTestParameter(final TestParameterValue parameterValue, final List<String> mandatoryParams) {
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
	}

	private String summaryOf(final IdObject idObject, final List<String> list) {
		final TestObjectDescription description = tc.descriptionOf(idObject);
		if (description == null) {
			return null;
		}

		return format(description.getHumanReadable(), list);
	}

	@VisibleForTesting
	public static String format(final String humanReadable, final List<String> paramsTexts) {
		final List<String> emptiedBlocks = new ArrayList<>();
		final List<String> formatParams;
		if (paramsTexts != null) {
			formatParams = paramsTexts.stream().flatMap(s -> stream((' ' + s + ' ').split("\\|"))).map(String::trim)
					.map(s -> s.replace("//", "/\\/")).collect(toList());
		} else {
			formatParams = null;
		}
		final Pattern blockPattern = Pattern.compile("//.*%s.*//");
		final Matcher blockMatcher = blockPattern.matcher(humanReadable);
		while (blockMatcher.find()) {
			emptiedBlocks.add(blockMatcher.group().replace("%s", ""));
		}
		String formatted = String.format(humanReadable, (formatParams != null) ? formatParams.toArray() : null);
		// remove empty blocks
		for (final String emptyBlock : emptiedBlocks) {
			formatted = formatted.replaceAll(emptyBlock, "");
		}
		formatted = formatted.replace("//", "").replace("/\\/", "//");
		return formatted;
	}

	public String processAllSteps() {
		return tc.getSteps().stream().map(this::process).collect(joining("\n"));
	}

}
