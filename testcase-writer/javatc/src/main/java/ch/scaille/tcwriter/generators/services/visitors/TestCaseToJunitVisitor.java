package ch.scaille.tcwriter.generators.services.visitors;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ch.scaille.generators.util.GenerationMetadata;
import ch.scaille.generators.util.JavaCodeGenerator;
import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.TestCaseException;
import ch.scaille.tcwriter.model.dictionary.TestApiParameter;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory.ParameterNature;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestParameterValue;
import ch.scaille.tcwriter.model.testcase.TestReference;
import ch.scaille.tcwriter.model.testcase.TestStep;
import ch.scaille.tcwriter.services.generators.visitors.HumanReadableVisitor;

/**
 * To generate a JUnit test from a Test Case
 */
public class TestCaseToJunitVisitor {

	private final Template template;

	private final Map<Integer, AtomicInteger> varIndex = new HashMap<>();

	private final Map<TestParameterValue, String> varNames = new IdentityHashMap<>();

	public TestCaseToJunitVisitor(final Template template) {
		this.template = template;
	}

	public Template visitTestCase(final TestCase tc, GenerationMetadata generationMetadata) throws TestCaseException {

		final var metadata = new HashMap<String, String>();
		final var javaContent = JavaCodeGenerator.inMemory();
		final var testSummaryVisitor = new HumanReadableVisitor(tc, false);

		for (final var step : tc.getSteps()) {
			javaContent.append("// Step ")
					.append(Integer.toString(step.getOrdinal()))
					.append(": ")
					.append(testSummaryVisitor.process(step))
					.eol();
			visitTestStep(javaContent, tc.getDictionary(), step);
		}

		metadata.put("package", tc.getPackage());
		metadata.put("testName", tc.getName());
		metadata.put("testContent", javaContent.toString());
		return template.apply(metadata, JavaCodeGenerator.toSourceFilename(tc.getPackage(), tc.getName()),
				generationMetadata);
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
		stepContentCode
				.addMethodCall(step.getActor().getName(), step.getAction().getName(),
						g -> addParameterValuesToCall(g, step, step.getParametersValue(),
								step.getAction().getParameters()))
				.eos()
				.eol();

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

		final var parameterVarName = varNameOf(step, paramValue);
		parametersContentCode.addVarAssign(valuefactory.getParameterType(), parameterVarName) //
				.addMethodCall(valuefactory.getName(),
						g -> addParameterValuesToCall(g, step, paramValue.getComplexTypeValues().values(),
								valuefactory.getMandatoryParameters()))
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
		switch (parameterValue.getValueFactory()) {
		case TestParameterFactory f when f.getNature() == ParameterNature.TEST_API ->
			parametersContent.append(varNameOf(step, parameterValue));

		case TestParameterFactory f when f.getNature() == ParameterNature.SIMPLE_TYPE
				&& String.class.getName().equals(f.getParameterType()) ->
			parametersContent.append("\"").append(parameterValue.getSimpleValue()).append("\"");

		case TestParameterFactory f when f.getNature() == ParameterNature.SIMPLE_TYPE
				&& (Long.class.getName().equals(f.getParameterType())
						|| Long.TYPE.getName().equals(f.getParameterType())) ->
			parametersContent.append(parameterValue.getSimpleValue()).append("L");

		case TestParameterFactory f when f.getNature() == ParameterNature.SIMPLE_TYPE ->
			parametersContent.append(parameterValue.getSimpleValue());

		case TestReference f when f.getNature() == ParameterNature.REFERENCE -> parametersContent.append(f.getName());
		default -> throw new TestCaseException("Parameter value is not set: " + parameterValue.getValueFactory());
		}
	}

	private String varNameOf(final TestStep step, final TestParameterValue testValue) {
		final var nextIndex = varIndex.computeIfAbsent(step.getOrdinal(), s -> new AtomicInteger(1)).getAndIncrement();
		return varNames.computeIfAbsent(testValue, v -> "step" + step.getOrdinal() + "_var" + nextIndex);
	}
}
