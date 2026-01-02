package ch.scaille.tcwriter.services.generators.visitors;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.Objects;
import java.util.regex.Pattern;

import ch.scaille.tcwriter.model.dictionary.ParameterNature;
import com.google.common.annotations.VisibleForTesting;

import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestReference;
import ch.scaille.tcwriter.model.testcase.TestStep;

/**
 * Formats human readable text.
 * <p>
 * A pattern //some text %s more text // indicates that either %s is replaced, or the entire text must not
 * appear if the value of %s is empty
 * 
 */
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
		if (actorSummary.isEmpty()) {
			actorSummary = summaryOf(step.getRole(), null);
		}
		final var result = new StringBuilder();
		if (withStepNumbers) {
			result.append(step.getOrdinal()).append(". ");
		}
		result.append("As ")
				.append(actorSummary)
				.append(", ")
				.append(summaryOf(step.getAction(),
						step.getParametersValue()
								.stream()
								.map(this::processTestParameter)
								.filter(Objects::nonNull)
								.toList()));

		return result.toString();
	}

	private String processTestParameter(final TestParameterValue parameterValue) {
		return switch (parameterValue.getValueFactory()) {
		case TestReference testRef ->
			"[" + testRef.toDescription().humanReadable() + ": " + parameterValue.getSimpleValue() + "]";
			
		case TestParameterFactory f when f.getNature() == ParameterNature.SIMPLE_TYPE &&
				(Boolean.class.getName().equals(f.getParameterType()) || Boolean.TYPE.getName().equals(f.getParameterType())) ->
			Boolean.TRUE.toString().equals(parameterValue.getSimpleValue()) ? "yes" : "no";
			
		case TestParameterFactory f when f.getNature() == ParameterNature.SIMPLE_TYPE ->
			parameterValue.getSimpleValue();
			
		case TestParameterFactory f when f.getNature() == ParameterNature.TEST_API ->
			processTestParameter(parameterValue, parameterValue.getValueFactory()
					.getMandatoryParameters()
					.stream()
					.map(p -> processTestParameter(parameterValue.getComplexTypeValues().get(p.getId())))
					.toList());
		default ->
			"";
		};

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
		return format(tc.descriptionOf(idObject).humanReadable(), list);
	}

	@VisibleForTesting
	public static String format(final String humanReadable, final List<String> paramsTexts) {
		final var emptiedBlocks = new ArrayList<String>();
		final List<String> formatParams;
		if (paramsTexts != null) {
			formatParams = paramsTexts.stream()
					.flatMap(s -> stream((' ' + s + ' ').split("\\|")))
					.map(String::trim)
					.map(s -> s.replace("//", "/\\/"))
					.toList();
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
		} catch (MissingFormatArgumentException _) {
			return "Some parameter values are missing in " + humanReadable;
		}
	}

	public String processAllSteps() {
		return tc.getSteps().stream().map(this::process).collect(joining("\n"));
	}

}
