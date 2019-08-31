package ch.skymarshall.tcwriter.generators.visitors;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.skymarshall.util.generators.JavaCodeGenerator;
import org.skymarshall.util.generators.Template;

import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterDefinition;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterType;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;

public class JunitTestCaseVisitor {

	private final Template template;

	private int varCount = 0;

	private final Map<TestParameterValue, String> varNames = new HashMap<>();

	private final boolean withController;

	public JunitTestCaseVisitor(final Template template, final boolean withController) {
		this.template = template;
		this.withController = withController;
	}

	public String visitTestCase(final TestCase tc) throws IOException, TestCaseException {

		final Map<String, String> properties = new HashMap<>();
		if (withController) {
			final JavaCodeGenerator remoteControlCode = new JavaCodeGenerator();

			remoteControlCode.append("private ITestExecutionController testExecutionController;").newLine().newLine()
					.append("@org.junit.Rule").newLine()//
					.append("public org.junit.rules.TestWatcher testWatcher = new org.junit.rules.TestWatcher() {")
					.newLine() //
					.append("	@Override").newLine() //
					.append("	protected void failed(final Throwable e, final org.junit.runner.Description description) {")
					.newLine() //
					.append("		super.failed(e, description);").newLine() //
					.append("		testExecutionController.notifyError(e);").newLine() //
					.append("	}").newLine() //
					.append("};").newLine() //
					.append("@org.junit.Before").newLine() //
					.append("public void prepareController() throws IOException {").newLine() //
					.append("	testExecutionController = TestExecutionController.controller();").newLine() //
					.append("}").newLine();
			properties.put("remoteControl", remoteControlCode.toString());
		} else {
			properties.put("remoteControl", "");
		}

		final HumanReadableVisitor testSummaryVisitor = new HumanReadableVisitor(tc);

		final JavaCodeGenerator javaContent = new JavaCodeGenerator();

		if (withController) {
			javaContent.add("testExecutionController.beforeTestExecution();").newLine();
		}

		for (final TestStep step : tc.getSteps()) {
			javaContent.append("// Step ").append(Integer.toString(step.getOrdinal())).append(": ")
					.append(testSummaryVisitor.process(step)).newLine();
			visitTestStep(javaContent, tc.getModel(), step);
		}

		properties.put("package", tc.getFolder());
		properties.put("testName", tc.getName());
		properties.put("testContent", javaContent.toString());
		return template.apply(properties).generate();
	}

	private void visitTestStep(final JavaCodeGenerator javaContent, final TestModel model, final TestStep step)
			throws IOException, TestCaseException {
		final StringBuilder comment = new StringBuilder();

		if (withController) {
			javaContent.add("testExecutionController.beforeStepExecution(" + step.getOrdinal() + ");").newLine();
		}

		final JavaCodeGenerator stepContent = new JavaCodeGenerator();

		for (final TestParameterValue stepParamValue : step.getParametersValue()) {
			visitTestValue(stepContent, model, stepParamValue);
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

		if (withController) {
			javaContent.add("testExecutionController.afterStepExecution(" + step.getOrdinal() + ");").newLine()
					.newLine();
		}

	}

	private void visitTestValue(final JavaCodeGenerator javaContent, final TestModel model,
			final TestParameterValue paramValue) throws IOException, TestCaseException {

		final TestParameterDefinition param = paramValue.getValueDefinition();
		if (param.getNature().isSimpleValue()) {
			return;
		}

		final JavaCodeGenerator parametersContent = new JavaCodeGenerator();

		visitTestValueParams(parametersContent, model, paramValue.getComplexTypeValues());

		final String parameterVarName = varNameFor(paramValue);
		parametersContent.append(param.getType()).append(" ").append(parameterVarName).append(" = ")
				.append(param.getName()).append("(");
		addParameterValuesToCall(parametersContent, paramValue.getComplexTypeValues().values(),
				param.getMandatoryParameters());
		parametersContent.append(");").newLine();
		addSetters(parametersContent, parameterVarName, paramValue.getComplexTypeValues().values(),
				param.getOptionalParameters());
		javaContent.append(parametersContent.toString());
	}

	private void visitTestValueParams(final JavaCodeGenerator parametersContent, final TestModel model,
			final Map<String, TestParameterValue> testObjectValues) throws IOException, TestCaseException {
		for (final TestParameterValue testObjectValue : testObjectValues.values()) {
			// No need to define a variable
			if (testObjectValue.getValueDefinition().getNature().isSimpleValue()) {
				// Simple value
				continue;
			}
			visitTestValue(parametersContent, model, testObjectValue);
		}
	}

	private void addParameterValuesToCall(final JavaCodeGenerator parametersContent,
			final Collection<TestParameterValue> parameterValues, final List<TestParameterType> filter)
			throws IOException, TestCaseException {
		final Set<String> filterIds = filter.stream().map(IdObject::getId).collect(Collectors.toSet());
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

	private void addSetters(final JavaCodeGenerator parametersContent, final String parameterVarName,
			final Collection<TestParameterValue> parameterValues, final List<TestParameterType> filter)
			throws IOException, TestCaseException {
		final Map<String, TestParameterType> filteredMap = filter.stream()
				.collect(Collectors.toMap(IdObject::getId, t -> t));
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
			throws IOException, TestCaseException {
		switch (parameterValue.getValueDefinition().getNature()) {
		case TEST_API_TYPE:
			parametersContent.append(varNameFor(parameterValue));
			break;
		case SIMPLE_TYPE:
			final String valueType = parameterValue.getValueDefinition().getType();
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
			parametersContent.append(parameterValue.getSimpleValue());
			break;
		default:
			throw new TestCaseException("Parameter value is not set");
		}
	}

	private String varNameFor(final TestParameterValue testValue) {
		return varNames.computeIfAbsent(testValue, v -> "var" + (varCount++));
	}
}
