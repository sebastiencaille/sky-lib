package ch.skymarshall.tcwriter.generators;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.skymarshall.util.generators.JavaCodeGenerator;
import org.skymarshall.util.generators.Template;

import ch.skymarshall.tcwriter.generators.model.TestCase;
import ch.skymarshall.tcwriter.generators.model.TestModel;
import ch.skymarshall.tcwriter.generators.model.TestParameter;
import ch.skymarshall.tcwriter.generators.model.TestParameterType;
import ch.skymarshall.tcwriter.generators.model.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.TestStep;

public class JavaGenerationVisitor {

	private final Template template;

	private int varCount = 0;

	private final Map<TestParameterValue, String> varNames = new HashMap<>();

	public JavaGenerationVisitor(final Template template) {
		this.template = template;
	}

	public String visitTestCase(final TestCase tc) throws IOException {
		final JavaCodeGenerator javaContent = new JavaCodeGenerator();
		for (final TestStep step : tc.getSteps()) {
			visitTestStep(javaContent, tc.getModel(), step);
		}

		final Map<String, String> properties = new HashMap<>();
		properties.put("package", tc.getFolder());
		properties.put("testName", tc.getName());
		properties.put("testContent", javaContent.toString());
		return template.apply(properties).generate();
	}

	private void visitTestStep(final JavaCodeGenerator javaContent, final TestModel model, final TestStep step)
			throws IOException {
		final StringBuilder comment = new StringBuilder();
		comment.append(
				"// Step " + step.getOrdinal() + " - " + step.getActor() + ": " + model.descriptionOf(step.getAction()))
				.append("\n");

		final JavaCodeGenerator stepContent = new JavaCodeGenerator();

		for (final TestParameterValue stepParamValue : step.getParametersValue()) {
			visitTestValue(stepContent, comment, model, stepParamValue);
		}

		stepContent.append(step.getActor().getName()).append(".").append(step.getAction().getName()).append("(");

		addParameterValuesToCall(stepContent, step.getParametersValue(), step.getAction().getParameters());
		stepContent.append(");").newLine().newLine();

		javaContent.append(comment.toString());
		javaContent.append(stepContent.toString());
	}

	private void visitTestValue(final JavaCodeGenerator javaContent, final StringBuilder comment, final TestModel model,
			final TestParameterValue paramValue) throws IOException {

		final TestParameter param = paramValue.getTestParameter();
		if (param.isSimpleType()) {
			return;
		}

		final JavaCodeGenerator parametersContent = new JavaCodeGenerator();

		visitTestValueParams(parametersContent, comment, model, paramValue.getComplexTypeValues());

		comment.append("//    ").append(model.descriptionOf(param.getType())).append("\n");

		final String parameterVarName = varNameFor(paramValue);
		parametersContent.append(param.getType()).append(" ").append(parameterVarName).append(" = ")
				.append(param.getName()).append("(");
		addParameterValuesToCall(parametersContent, paramValue.getComplexTypeValues().values(),
				param.getMandatoryParameters());
		parametersContent.append(");").newLine();
		addSetters(parametersContent, comment, model, parameterVarName, paramValue.getComplexTypeValues().values(),
				param.getOptionalParameters());
		javaContent.append(parametersContent.toString());
	}

	private void visitTestValueParams(final JavaCodeGenerator parametersContent, final StringBuilder comment,
			final TestModel model, final Map<String, TestParameterValue> testObjectValues) throws IOException {
		for (final TestParameterValue testObjectValue : testObjectValues.values()) {
			// No need to define a variable
			if (testObjectValue.getTestParameter().isSimpleType()) {
				// Simple value
				continue;
			}
			visitTestValue(parametersContent, comment, model, testObjectValue);
		}
	}

	private void addParameterValuesToCall(final JavaCodeGenerator parametersContent,
			final Collection<TestParameterValue> parameterValues, final List<TestParameterType> filter)
			throws IOException {
		final Set<String> filterIds = filter.stream().map(f -> f.getId()).collect(Collectors.toSet());
		String sep = "";
		for (final TestParameterValue parameterValue : parameterValues) {
			if (!filterIds.contains(parameterValue.getId())) {
				continue;
			}
			parametersContent.add(sep);
			inlineValue(parametersContent, parameterValue);
			sep = ", ";
		}
	}

	private void addSetters(final JavaCodeGenerator parametersContent, final StringBuilder comment,
			final TestModel model, final String parameterVarName, final Collection<TestParameterValue> parameterValues,
			final List<TestParameterType> filter) throws IOException {
		final Map<String, TestParameterType> filteredMap = filter.stream()
				.collect(Collectors.toMap(t -> t.getId(), t -> t));
		for (final TestParameterValue parameterValue : parameterValues) {
			if (!filteredMap.containsKey(parameterValue.getId())) {
				continue;
			}
			final TestParameterType parameterType = filteredMap.get(parameterValue.getId());
			parametersContent.append(parameterVarName).append(".").append(parameterType.getName()).append("(");
			inlineValue(parametersContent, parameterValue);
			parametersContent.append(");").newLine();
		}
	}

	private void inlineValue(final JavaCodeGenerator parametersContent, final TestParameterValue parameterValue)
			throws IOException {
		if (parameterValue.getTestParameter().isSimpleType()) {
			final String valueType = parameterValue.getTestParameter().getType();
			final boolean isString = String.class.getName().equals(valueType);
			final boolean isLong = Long.class.getName().equals(valueType) || Long.TYPE.getName().equals(valueType);
			if (isString) {
				parametersContent.append("\"");
			}
			parametersContent.append(parameterValue.getSimpleValue());
			if (isString) {
				parametersContent.append("\"");
			} else if (isLong) {
				parametersContent.append("L");
			}
		} else {
			parametersContent.append(varNameFor(parameterValue));
		}
	}

	private String varNameFor(final TestParameterValue testValue) {
		return varNames.computeIfAbsent(testValue, v -> "var" + (varCount++));
	}
}
