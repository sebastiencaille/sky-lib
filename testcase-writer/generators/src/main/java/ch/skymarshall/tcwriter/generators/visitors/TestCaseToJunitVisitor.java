package ch.skymarshall.tcwriter.generators.visitors;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.TestCaseException;
import ch.skymarshall.tcwriter.generators.model.testapi.TestApiParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestDictionary;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestParameterValue;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;
import ch.skymarshall.util.generators.JavaCodeGenerator;
import ch.skymarshall.util.generators.Template;

public class TestCaseToJunitVisitor {

	private final Template template;

	private final Map<Integer, AtomicInteger> varIndex = new HashMap<>();

	private final Map<TestParameterValue, String> varNames = new IdentityHashMap<>();

	public TestCaseToJunitVisitor(final Template template) {
		this.template = template;
	}

	public Template visitTestCase(final TestCase tc) throws IOException, TestCaseException {

		final Map<String, String> properties = new HashMap<>();

		final HumanReadableVisitor testSummaryVisitor = new HumanReadableVisitor(tc, false);

		final JavaCodeGenerator javaContent = new JavaCodeGenerator();

		for (final TestStep step : tc.getSteps()) {
			javaContent.append("// Step ").append(Integer.toString(step.getOrdinal())).append(": ")
					.append(testSummaryVisitor.process(step)).eol();
			visitTestStep(javaContent, tc.getDictionary(), step);
		}

		properties.put("package", tc.getPackage());
		properties.put("testName", tc.getName());
		properties.put("testContent", javaContent.toString());
		return template.apply(properties, JavaCodeGenerator.classToSource(tc.getPackage(), tc.getName()));
	}

	private void visitTestStep(final JavaCodeGenerator javaContent, final TestDictionary model, final TestStep step)
			throws IOException, TestCaseException {
		final StringBuilder comment = new StringBuilder();

		final JavaCodeGenerator stepContent = new JavaCodeGenerator();

		for (final TestParameterValue stepParamValue : step.getParametersValue()) {
			visitTestParameterValue(stepContent, model, step, stepParamValue);
		}

		if (step.getReference() != null) {
			stepContent.addVarAssign(step.getReference().getType(), step.getReference().getName());
		}
		stepContent.addMethodCall(step.getActor().getName(), step.getAction().getName(),
				g -> addParameterValuesToCall(g, step, step.getParametersValue(), step.getAction().getParameters()))
				.eos().eol();

		javaContent.append(comment);
		javaContent.append(stepContent);
	}

	private void visitTestParameterValue(final JavaCodeGenerator javaContent, final TestDictionary model,
			final TestStep step, final TestParameterValue paramValue) throws IOException, TestCaseException {

		final TestParameterFactory factory = paramValue.getValueFactory();
		if (factory.getNature().isSimpleValue()) {
			return;
		}

		final JavaCodeGenerator parametersContent = new JavaCodeGenerator();

		visitTestValueParams(parametersContent, model, step, paramValue.getComplexTypeValues());

		final String parameterVarName = varNameFor(step, paramValue);
		parametersContent.addVarAssign(factory.getType(), parameterVarName) //
				.addMethodCall(factory.getName(), g -> addParameterValuesToCall(g, step,
						paramValue.getComplexTypeValues().values(), factory.getMandatoryParameters()))
				.eos();
		addOptionalParameters(step, parametersContent, parameterVarName, paramValue.getComplexTypeValues().values(),
				factory.getOptionalParameters());
		javaContent.append(parametersContent);
	}

	private void visitTestValueParams(final JavaCodeGenerator parametersContent, final TestDictionary model,
			final TestStep step, final Map<String, TestParameterValue> testObjectValues)
			throws IOException, TestCaseException {
		for (final TestParameterValue testObjectValue : testObjectValues.values()) {
			// No need to define a variable
			if (testObjectValue.getValueFactory().getNature().isSimpleValue()) {
				// Simple value
				continue;
			}
			visitTestParameterValue(parametersContent, model, step, testObjectValue);
		}
	}

	private void addParameterValuesToCall(final JavaCodeGenerator parametersContent, final TestStep step,
			final Collection<TestParameterValue> parameterValues, final List<TestApiParameter> filter)
			throws IOException, TestCaseException {
		final Set<String> filterIds = filter.stream().map(IdObject::getId).collect(Collectors.toSet());
		String sep = "";
		for (final TestParameterValue parameterValue : parameterValues) {
			if (!filterIds.contains(parameterValue.getApiParameterId())) {
				continue;
			}
			parametersContent.append(sep);
			inlineValue(parametersContent, step, parameterValue);
			sep = ", ";
		}
	}

	private void addOptionalParameters(final TestStep step, final JavaCodeGenerator parametersContent,
			final String parameterVarName, final Collection<TestParameterValue> parameterValues,
			final List<TestApiParameter> filter) throws IOException, TestCaseException {
		final Map<String, TestApiParameter> filteredMap = filter.stream()
				.collect(Collectors.toMap(IdObject::getId, t -> t));
		for (final TestParameterValue parameterValue : parameterValues) {
			if (!filteredMap.containsKey(parameterValue.getApiParameterId())) {
				continue;
			}
			final TestApiParameter parameterType = filteredMap.get(parameterValue.getApiParameterId());
			parametersContent.addMethodCall(parameterVarName, parameterType.getName(), g -> {
				if (parameterValue.getValueFactory().hasType()) {
					inlineValue(g, step, parameterValue);
				}
			}).eos();
		}
	}

	private void inlineValue(final JavaCodeGenerator parametersContent, final TestStep step,
			final TestParameterValue parameterValue) throws IOException, TestCaseException {
		switch (parameterValue.getValueFactory().getNature()) {
		case TEST_API:
			parametersContent.append(varNameFor(step, parameterValue));
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

	private String varNameFor(final TestStep step, final TestParameterValue testValue) {
		final int nextIndex = varIndex.computeIfAbsent(step.getOrdinal(), (s) -> new AtomicInteger(1))
				.getAndIncrement();
		return varNames.computeIfAbsent(testValue, v -> "step" + step.getOrdinal() + "_var" + nextIndex);
	}
}
