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
import ch.skymarshall.tcwriter.generators.model.TestObjectParameter;
import ch.skymarshall.tcwriter.generators.model.TestStep;
import ch.skymarshall.tcwriter.generators.model.TestValue;

public class JavaGenerationVisitor {

	private final Template template;

	private int varCount = 0;

	private final Map<TestValue, String> varNames = new HashMap<>();

	public JavaGenerationVisitor(final Template template) {
		this.template = template;
	}

	public String visit(final TestCase tc) throws IOException {
		final JavaCodeGenerator javaContent = new JavaCodeGenerator();
		for (final TestStep step : tc.getSteps()) {
			visit(javaContent, tc.getModel(), step);
		}

		final Map<String, String> properties = new HashMap<>();
		properties.put("package", tc.getFolder());
		properties.put("testName", tc.getName());
		properties.put("testContent", javaContent.toString());
		return template.apply(properties).generate();
	}

	private void visit(final JavaCodeGenerator javaContent, final TestModel model, final TestStep step)
			throws IOException {
		final StringBuilder comment = new StringBuilder();
		comment.append(
				"// Step " + step.getOrdinal() + " - " + step.getActor() + ": " + model.descriptionOf(step.getMethod()))
				.append("\n");

		final JavaCodeGenerator stepContent = new JavaCodeGenerator();

		for (final TestValue param : step.getParameters()) {
			visitParameter(stepContent, comment, model, param);
		}

		stepContent.append(step.getActor().getName()).append(".").append(step.getMethod().getName()).append("(");

		addParametersToCall(stepContent, step.getParameters(), step.getMethod().getParameters());
		stepContent.append(");").newLine().newLine();

		javaContent.append(comment.toString());
		javaContent.append(stepContent.toString());
	}

	private void visitParameter(final JavaCodeGenerator javaContent, final StringBuilder comment, final TestModel model,
			final TestValue param) throws IOException {

		if (param.getTestObject().isSimpleType()) {
			return;
		}

		final JavaCodeGenerator parametersContent = new JavaCodeGenerator();

		visitParams(parametersContent, comment, model, param.getTestObjectValues());

		comment.append("//    ").append(model.descriptionOf(param.getTestObject())).append("\n");
		;

		final String parameterVarName = varNameFor(param);
		parametersContent.append(param.getTestObject().getType()).append(" ").append(parameterVarName).append(" = ")
				.append(param.getTestObject().getName()).append("(");
		addParametersToCall(parametersContent, param.getTestObjectValues().values(),
				param.getTestObject().getMandatoryParameters());
		parametersContent.append(");").newLine();
		addSetters(parametersContent, comment, model, parameterVarName, param.getTestObjectValues().values(),
				param.getTestObject().getOptionalParameters());
		javaContent.append(parametersContent.toString());
	}

	private void visitParams(final JavaCodeGenerator parametersContent, final StringBuilder comment,
			final TestModel model, final Map<String, TestValue> testObjectValues) throws IOException {
		for (final TestValue testObjectValue : testObjectValues.values()) {
			// No need to define a variable
			if (testObjectValue.getTestObject().isSimpleType()) {
				continue;
			}
			visitParameter(parametersContent, comment, model, testObjectValue);
		}
	}

	private void addParametersToCall(final JavaCodeGenerator parametersContent,
			final Collection<TestValue> parameterValues, final List<TestObjectParameter> filter) throws IOException {
		final Set<String> filteredIds = filter.stream().map(f -> f.getId()).collect(Collectors.toSet());
		String sep = "";
		for (final TestValue parameterValue : parameterValues) {
			if (!filteredIds.contains(parameterValue.getId())) {
				continue;
			}
			parametersContent.add(sep);
			if (parameterValue.getTestObject().isSimpleType()) {
				parametersContent.append(parameterValue.getSimpleValue());
			} else {
				parametersContent.append(varNameFor(parameterValue));
			}
			sep = ", ";
		}
	}

	private void addSetters(final JavaCodeGenerator parametersContent, final StringBuilder comment,
			final TestModel model, final String parameterVarName, final Collection<TestValue> parameterValues,
			final List<TestObjectParameter> filter) throws IOException {
		final Set<String> filteredIds = filter.stream().map(f -> f.getId()).collect(Collectors.toSet());
		for (final TestValue parameterValue : parameterValues) {
			if (!filteredIds.contains(parameterValue.getTestObject().getId())) {
				continue;
			}
			parametersContent.append(parameterVarName).append(".").append(parameterValue.getTestObject().getName())
					.append("(");
			if (parameterValue.getTestObject().isSimpleType()) {
				parametersContent.append(parameterValue.getSimpleValue());
			} else {
				parametersContent.append(varNameFor(parameterValue));
			}
			parametersContent.append(");").newLine();
		}
	}

	private String varNameFor(final TestValue testValue) {
		return varNames.computeIfAbsent(testValue, v -> "var" + (varCount++));
	}
}
