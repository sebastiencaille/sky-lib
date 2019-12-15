package ch.skymarshall.tcwriter.generators.visitors;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.util.generators.JavaCodeGenerator;
import ch.skymarshall.util.generators.Template;

public class TestCaseToJunitVisitor {

	private final Template template;

	private int varIndex = 0;

	private final Map<TestParameterValue, String> varNames = new IdentityHashMap<>();

	public TestCaseToJunitVisitor(final Template template) {
		this.template = template;
	}

	public String visitTestCase(final TestCase tc) throws IOException, TestCaseException {

		final Map<String, String> properties = new HashMap<>();

		final HumanReadableVisitor testSummaryVisitor = new HumanReadableVisitor(tc);

		final JavaCodeGenerator javaContent = new JavaCodeGenerator();

		for (final TestStep step : tc.getSteps()) {
			javaContent.append("// Step ").append(Integer.toString(step.getOrdinal())).append(": ")
					.append(testSummaryVisitor.process(step)).newLine();
			visitTestStep(javaContent, tc.getModel(), step);
		}

		properties.put("package", tc.getPackage());
		properties.put("testName", tc.getName());
		properties.put("testContent", javaContent.toString());
		return template.apply(properties).generate();
	}

	private void visitTestStep(final JavaCodeGenerator javaContent, final TestModel model, final TestStep step)
			throws IOException, TestCaseException {
		final StringBuilder comment = new StringBuilder();

		final JavaCodeGenerator stepContent = new JavaCodeGenerator();

		for (final TestParameterValue stepParamValue : step.getParametersValue()) {
			visitTestParameterValue(stepContent, model, stepParamValue);
		}

		if (step.getReference() != null) {
			stepContent.append(step.getReference().getType()).append(" ").append(step.getReference().getName())
					.append(" = ");
		}
		stepContent.append(step.getActor().getName()).append(".").append(step.getAction().getName()).append("(");

		addParameterValuesToCall(stepContent, step.getParametersValue(), step.getAction().getParameters());
		stepContent.append(");").newLine().newLine();

		javaContent.append(comment.toString());
		javaContent.append(stepContent.toString());
	}

	private void visitTestParameterValue(final JavaCodeGenerator javaContent, final TestModel model,
			final TestParameterValue paramValue) throws IOException, TestCaseException {

		final TestParameterFactory factory = paramValue.getValueFactory();
		if (factory.getNature().isSimpleValue()) {
			return;
		}

		final JavaCodeGenerator parametersContent = new JavaCodeGenerator();

		visitTestValueParams(parametersContent, model, paramValue.getComplexTypeValues());

		final String parameterVarName = varNameFor(paramValue);
		parametersContent.append(factory.getType()).append(" ").append(parameterVarName).append(" = ")
				.append(factory.getName()).append("(");
		addParameterValuesToCall(parametersContent, paramValue.getComplexTypeValues().values(),
				factory.getMandatoryParameters());
		parametersContent.append(");").newLine();
		addOptionalParameters(parametersContent, parameterVarName, paramValue.getComplexTypeValues().values(),
				factory.getOptionalParameters());
		javaContent.append(parametersContent.toString());
	}

	private void visitTestValueParams(final JavaCodeGenerator parametersContent, final TestModel model,
			final Map<String, TestParameterValue> testObjectValues) throws IOException, TestCaseException {
		for (final TestParameterValue testObjectValue : testObjectValues.values()) {
			// No need to define a variable
			if (testObjectValue.getValueFactory().getNature().isSimpleValue()) {
				// Simple value
				continue;
			}
			visitTestParameterValue(parametersContent, model, testObjectValue);
		}
	}

	private void addParameterValuesToCall(final JavaCodeGenerator parametersContent,
			final Collection<TestParameterValue> parameterValues, final List<TestApiParameter> filter)
			throws IOException, TestCaseException {
		final Set<String> filterIds = filter.stream().map(IdObject::getId).collect(Collectors.toSet());
		String sep = "";
		for (final TestParameterValue parameterValue : parameterValues) {
			if (!filterIds.contains(parameterValue.getApiParameterId())) {
				continue;
			}
			parametersContent.add(sep);
			inlineValue(parametersContent, parameterValue);
			sep = ", ";
		}
	}

	private void addOptionalParameters(final JavaCodeGenerator parametersContent, final String parameterVarName,
			final Collection<TestParameterValue> parameterValues, final List<TestApiParameter> filter)
			throws IOException, TestCaseException {
		final Map<String, TestApiParameter> filteredMap = filter.stream()
				.collect(Collectors.toMap(IdObject::getId, t -> t));
		for (final TestParameterValue parameterValue : parameterValues) {
			if (!filteredMap.containsKey(parameterValue.getApiParameterId())) {
				continue;
			}
			final TestApiParameter parameterType = filteredMap.get(parameterValue.getApiParameterId());
			parametersContent.append(parameterVarName).append(".").append(parameterType.getName()).append("(");
			if (parameterValue.getValueFactory().hasType()) {
				inlineValue(parametersContent, parameterValue);
			}
			parametersContent.append(");").newLine();
		}
	}

	private void inlineValue(final JavaCodeGenerator parametersContent, final TestParameterValue parameterValue)
			throws IOException, TestCaseException {
		switch (parameterValue.getValueFactory().getNature()) {
		case TEST_API:
			parametersContent.append(varNameFor(parameterValue));
			break;
		case SIMPLE_TYPE:
			final String valueType = parameterValue.getValueFactory().getType();
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
			break;
		case REFERENCE:
			parametersContent.append(parameterValue.getValueFactory().getName());
			break;
		default:
			throw new TestCaseException("Parameter value is not set");
		}
	}

	private String varNameFor(final TestParameterValue testValue) {
		varIndex++;
		return varNames.computeIfAbsent(testValue, v -> "var" + varIndex);
	}
}