package ch.scaille.tcwriter.generators.visitors;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ch.scaille.generators.util.JavaCodeGenerator;
import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.dictionary.TestApiParameter;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestStep;

public class TestCaseToJunitVisitor {

	private final Template template;

	private final Map<Integer, AtomicInteger> varIndex = new HashMap<>();

	private final Map<TestParameterValue, String> varNames = new IdentityHashMap<>();

	public TestCaseToJunitVisitor(final Template template) {
		this.template = template;
	}

	public Template visitTestCase(final TestCase tc) throws TestCaseException {

		final var properties = new HashMap<String, String>();

		final var testSummaryVisitor = new HumanReadableVisitor(tc, false);

		final var javaContent = JavaCodeGenerator.inMemory();

		for (final var step : tc.getSteps()) {
			javaContent.append("// Step ").append(Integer.toString(step.getOrdinal())).append(": ")
					.append(testSummaryVisitor.process(step)).eol();
			visitTestStep(javaContent, tc.getDictionary(), step);
		}

		properties.put("package", tc.getPackage());
		properties.put("testName", tc.getName());
		properties.put("testContent", javaContent.toString());
		return template.apply(properties, JavaCodeGenerator.classToSource(tc.getPackage(), tc.getName()));
	}

	private void visitTestStep(final JavaCodeGenerator<RuntimeException> javaContent, final TestDictionary model,
			final TestStep step) throws TestCaseException {
		final var comment = new StringBuilder();
		final var stepContentCode = JavaCodeGenerator.inMemory();

		for (final var stepParamValue : step.getParametersValue()) {
			visitTestParameterValue(stepContentCode, model, step, stepParamValue);
		}

		if (step.getReference() != null) {
			stepContentCode.addVarAssign(step.getReference().getParameterType(), step.getReference().getName());
		}
		stepContentCode.addMethodCall(step.getActor().getName(), step.getAction().getName(),
				g -> addParameterValuesToCall(g, step, step.getParametersValue(), step.getAction().getParameters()))
				.eos().eol();

		javaContent.append(comment);
		javaContent.append(stepContentCode);
	}

	private void visitTestParameterValue(final JavaCodeGenerator<RuntimeException> javaContent,
			final TestDictionary model, final TestStep step, final TestParameterValue paramValue)
			throws TestCaseException {

		final var valuefactory = paramValue.getValueFactory();
		if (valuefactory.getNature().isSimpleValue()) {
			return;
		}

		final var parametersContentCode = JavaCodeGenerator.inMemory();

		visitTestValueParams(parametersContentCode, model, step, paramValue.getComplexTypeValues());

		final var parameterVarName = varNameFor(step, paramValue);
		parametersContentCode.addVarAssign(valuefactory.getParameterType(), parameterVarName) //
				.addMethodCall(valuefactory.getName(), g -> addParameterValuesToCall(g, step,
						paramValue.getComplexTypeValues().values(), valuefactory.getMandatoryParameters()))
				.eos();
		addOptionalParameters(step, parametersContentCode, parameterVarName, paramValue.getComplexTypeValues().values(),
				valuefactory.getOptionalParameters());
		javaContent.append(parametersContentCode);
	}

	private void visitTestValueParams(final JavaCodeGenerator<RuntimeException> parametersContent,
			final TestDictionary model, final TestStep step, final Map<String, TestParameterValue> testObjectValues)
			throws TestCaseException {
		for (final var testObjectValue : testObjectValues.values()) {
			// No need to define a variable
			if (testObjectValue.getValueFactory().getNature().isSimpleValue()) {
				// Simple value
				continue;
			}
			visitTestParameterValue(parametersContent, model, step, testObjectValue);
		}
	}

	private void addParameterValuesToCall(final JavaCodeGenerator<RuntimeException> parametersContent,
			final TestStep step, final Collection<TestParameterValue> parameterValues,
			final List<TestApiParameter> filter) throws TestCaseException {
		final var filterIds = filter.stream().map(IdObject::getId).collect(toSet());
		var sep = "";
		for (final var parameterValue : parameterValues) {
			if (!filterIds.contains(parameterValue.getApiParameterId())) {
				continue;
			}
			parametersContent.append(sep);
			inlineValue(parametersContent, step, parameterValue);
			sep = ", ";
		}
	}

	private void addOptionalParameters(final TestStep step, final JavaCodeGenerator<RuntimeException> parametersContent,
			final String parameterVarName, final Collection<TestParameterValue> parameterValues,
			final List<TestApiParameter> filter) throws TestCaseException {
		final var filteredMap = filter.stream().collect(toMap(IdObject::getId, t -> t));
		for (final var parameterValue : parameterValues) {
			if (!filteredMap.containsKey(parameterValue.getApiParameterId())) {
				continue;
			}
			final var parameterType = filteredMap.get(parameterValue.getApiParameterId());
			parametersContent.addMethodCall(parameterVarName, parameterType.getName(), g -> {
				if (parameterValue.getValueFactory().hasType()) {
					inlineValue(g, step, parameterValue);
				}
			}).eos();
		}
	}

	private void inlineValue(final JavaCodeGenerator<RuntimeException> parametersContent, final TestStep step,
			final TestParameterValue parameterValue) throws TestCaseException {
		switch (parameterValue.getValueFactory().getNature()) {
		case TEST_API:
			parametersContent.append(varNameFor(step, parameterValue));
			break;
		case SIMPLE_TYPE:
			final var valueType = parameterValue.getValueFactory().getParameterType();
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
		final int nextIndex = varIndex.computeIfAbsent(step.getOrdinal(), s -> new AtomicInteger(1)).getAndIncrement();
		return varNames.computeIfAbsent(testValue, v -> "step" + step.getOrdinal() + "_var" + nextIndex);
	}
}
