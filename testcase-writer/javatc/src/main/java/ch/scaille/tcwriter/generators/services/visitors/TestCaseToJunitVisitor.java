package ch.scaille.tcwriter.generators.services.visitors;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

	private void visitTestStep(final JavaCodeGenerator<RuntimeException> javaContent, final TestDictionary dictionary,
			final TestStep step) throws TestCaseException {
		final var comment = new StringBuilder();
		final var stepCode = JavaCodeGenerator.inMemory();

		if (step.getReference() != null) {
			stepCode.addVarAssign(step.getReference().getParameterType(), step.getReference().getName());
		}
		stepCode
				.addMethodCall(step.getActor().getName(), step.getAction().getName(),
						paramsCode -> 
					addParameterValuesToCall(javaContent, paramsCode, step, dictionary, step.getParametersValue(),
									step.getAction().getParameters()))
				.eos()
				.eol();

		javaContent.append(comment);
		javaContent.append(stepCode);
	}

	/**
	 * Returns the call to the factory, either inlined or using a variable created in testCode
	 */
	private String visitTestParameterValue(final JavaCodeGenerator<RuntimeException> testCode,
			final TestDictionary dictionary, final TestStep step, final TestParameterValue paramValue)
			throws TestCaseException {

		final var valueFactory = paramValue.getValueFactory();

		// Check if we have optional parameters
		final var optionalParameters = new HashSet<>(paramValue.getComplexTypeValues().keySet());
		optionalParameters.retainAll(valueFactory.getOptionalParameters().stream().map(TestApiParameter::getId).collect(toSet()));
		final var hasOptionalParameters = !optionalParameters.isEmpty();
		
		final var mandatoryParamContentCode = JavaCodeGenerator.inMemory();

		// Write the variable if needed
		final String parameterVarName;
		if (hasOptionalParameters) {
			parameterVarName = varNameOf(step, paramValue);
			mandatoryParamContentCode.addVarAssign("var", parameterVarName);
		} else {
			parameterVarName = null;
		}
		// Write the call to the factory, with mandatory parameters
		mandatoryParamContentCode.addMethodCall(valueFactory.getName(),
						g -> addParameterValuesToCall(null, g, step, 
								dictionary, paramValue.getComplexTypeValues().values(),
								valueFactory.getMandatoryParameters()));
		if (hasOptionalParameters) {
			// Put the call before the step,  
			testCode.append(mandatoryParamContentCode.eos().toString());
			addOptionalParameters(testCode, step, parameterVarName, paramValue.getComplexTypeValues().values(),
					valueFactory.getOptionalParameters());
			return Objects.requireNonNull(parameterVarName);
		}
		return mandatoryParamContentCode.toString();
	}

	private void addParameterValuesToCall(
			final JavaCodeGenerator<RuntimeException> javaContent,
			final JavaCodeGenerator<RuntimeException> parametersContent,
			final TestStep step,
			final TestDictionary dictionary,
			final Collection<TestParameterValue> parameterValues,
			final List<TestApiParameter> filter) throws TestCaseException {
		final var filterIds = filter.stream().map(IdObject::getId).collect(toSet());
		var sep = "";
		for (final var parameterValue : parameterValues) {
			if (!filterIds.contains(parameterValue.getApiParameterId())) {
				continue;
			}
			parametersContent.append(sep);
			inlineValue(javaContent, parametersContent, step, dictionary, parameterValue);
			sep = ", ";
		}
	}

	private void addOptionalParameters(final JavaCodeGenerator<RuntimeException> parametersContent,
									   final TestStep step,
			final String parameterVarName, final Collection<TestParameterValue> parameterValues,
			final List<TestApiParameter> optionalParameters) throws TestCaseException {
		final var parametersValueFilterMap = optionalParameters.stream().collect(toMap(IdObject::getId, t -> t));
		for (final var parameterValue : parameterValues) {
			if (!parametersValueFilterMap.containsKey(parameterValue.getApiParameterId())) {
				continue;
			}
			final var parameterType = parametersValueFilterMap.get(parameterValue.getApiParameterId());
			parametersContent.addMethodCall(parameterVarName, parameterType.getName(), g -> {
				if (parameterValue.getValueFactory().hasType()) {
					inlineValue(null, g, step, null, parameterValue);
				}
			}).eos();
		}
	}

	private void inlineValue(final JavaCodeGenerator<RuntimeException> stepContentCode, 
							 final JavaCodeGenerator<RuntimeException> parametersContent,
							 final TestStep step,
							 final TestDictionary dictionary,
							 final TestParameterValue parameterValue) throws TestCaseException {
		switch (parameterValue.getValueFactory()) {

		case TestParameterFactory f when f.getNature() == ParameterNature.TEST_API ->
			parametersContent.append(visitTestParameterValue(stepContentCode, dictionary, step, parameterValue));

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
		return varNames.computeIfAbsent(testValue, v -> String.format("step_%s_%s_%s" , step.getOrdinal(), testValue.getValueFactory().getName().replace('.', '_'),
				nextIndex));
	}
}
