package ch.scaille.tcwriter.generators.visitors;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.Objects;
import java.util.regex.Pattern;

import com.google.common.annotations.VisibleForTesting;

import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestReference;
import ch.scaille.tcwriter.model.testcase.TestStep;

public class HumanReadableVisitor {

	private static final Pattern BLOCK_PATTERN = Pattern.compile("//.*%s.*//");

	private final TestCase tc;
	private final boolean withStepNumbers;

	public HumanReadableVisitor(final TestCase tc, final boolean withStepNumbers) {
		this.tc = tc;
		this.withStepNumbers = withStepNumbers;
	}

	public String process(final TestStep step) {
		var actorSummary = summaryOf(step.getActor(), null);
		if (actorSummary == null) {
			actorSummary = summaryOf(step.getRole(), null);
		}
		final var result = new StringBuilder();
		if (withStepNumbers) {
			result.append(step.getOrdinal()).append(". ");
		}
		result.append("As ").append(actorSummary).append(", ").append(summaryOf(step.getAction(),
				step.getParametersValue().stream().map(this::processTestParameter).filter(Objects::nonNull).toList()));

		return result.toString();
	}

	private String processTestParameter(final TestParameterValue parameterValue) {
		switch (parameterValue.getValueFactory().getNature()) {
		case REFERENCE:
			final var testRef = (TestReference) parameterValue.getValueFactory();
			return "[" + testRef.toDescription().getHumanReadable() + ": " + parameterValue.getSimpleValue() + "]";
		case SIMPLE_TYPE:
			final var type = parameterValue.getValueFactory().getParameterType();
			if (Boolean.class.getName().equals(type) || Boolean.TYPE.getName().equals(type)) {
				return Boolean.TRUE.toString().equals(parameterValue.getSimpleValue()) ? "yes" : "no";
			}
			return parameterValue.getSimpleValue();
		case TEST_API:
			final var mandatoryParams = parameterValue.getValueFactory().getMandatoryParameters().stream()
					.map(p -> processTestParameter(parameterValue.getComplexTypeValues().get(p.getId()))).toList();
			return processTestParameter(parameterValue, mandatoryParams);
		default:
			return "";
		}

	}

	private String processTestParameter(final TestParameterValue parameterValue, final List<String> mandatoryParams) {
		final var optionals = new StringBuilder();
		var sep = "(";
		boolean hasOptionals = false;
		for (final var optionalParameter : parameterValue.getValueFactory().getOptionalParameters()) {
			final var optionalParameterValue = parameterValue.getComplexTypeValues().get(optionalParameter.getId());
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
		return summaryOf(parameterValue.getValueFactory(), mandatoryParams) + ((hasOptionals) ? " " + optionals : "");
	}

	private String summaryOf(final IdObject idObject, final List<String> list) {
		final var description = tc.descriptionOf(idObject);
		if (description == null) {
			return null;
		}

		return format(description.getHumanReadable(), list);
	}

	@VisibleForTesting
	public static String format(final String humanReadable, final List<String> paramsTexts) {
		final var emptiedBlocks = new ArrayList<String>();
		final List<String> formatParams;
		if (paramsTexts != null) {
			formatParams = paramsTexts.stream().flatMap(s -> stream((' ' + s + ' ').split("\\|"))).map(String::trim)
					.map(s -> s.replace("//", "/\\/")).toList();
		} else {
			formatParams = null;
		}

		final var blockMatcher = BLOCK_PATTERN.matcher(humanReadable);
		while (blockMatcher.find()) {
			emptiedBlocks.add(blockMatcher.group().replace("%s", ""));
		}
		try {
			var formatted = String.format(humanReadable, (formatParams != null) ? formatParams.toArray() : null);
			// remove empty blocks
			for (final var emptyBlock : emptiedBlocks) {
				formatted = formatted.replaceAll(emptyBlock, "");
			}
			formatted = formatted.replace("//", "").replace("/\\/", "//");
			return formatted;
		} catch (MissingFormatArgumentException e) {
			return "Some parameter values are missing";
		}
	}

	public String processAllSteps() {
		return tc.getSteps().stream().map(this::process).collect(joining("\n"));
	}

}
