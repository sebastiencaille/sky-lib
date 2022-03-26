package ch.scaille.tcwriter.generators;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import ch.scaille.tcwriter.model.testapi.TestDictionary;
import ch.scaille.tcwriter.model.testcase.TestCase;
import ch.scaille.tcwriter.model.testcase.TestStep;
import ch.scaille.util.helpers.LambdaExt;

public class Helper {

	private Helper() {
	}

	private static final char EOL = '\n';

	static String dumpModel(final TestDictionary model) {
		final var builder = new StringBuilder();
		builder.append(model.toString()).append(EOL);
		for (final var actor : model.getRoles().values()) {
			builder.append("  ").append(model.descriptionOf(actor)).append(": ").append(actor).append(EOL);
			actor.getActions().forEach(api -> builder.append("    ").append(model.descriptionOf(api)).append(": ")
					.append(api).append(EOL));

		}
		for (final var parameterFactory : model.getParameterFactories().values()) {
			builder.append("  ").append(model.descriptionOf(parameterFactory)).append(": ").append(parameterFactory)
					.append(EOL);
			parameterFactory.getMandatoryParameters().forEach(api -> builder.append("    mandatory: ")
					.append(model.descriptionOf(api)).append(": ").append(api).append(EOL));
			parameterFactory.getOptionalParameters().forEach(api -> builder.append("    optional: ")
					.append(model.descriptionOf(api)).append(": ").append(api).append(EOL));
		}
		return builder.toString();
	}

	static String dumpTestCase(final TestCase testCase) {
		final var result = new StringBuilder();
		for (final var step : testCase.getSteps()) {
			result.append(step.getOrdinal()).append(": ").append(step.getActor().getName()).append(" ")
					.append(step.getAction().getName()).append(EOL);
			step.getParametersValue().forEach(v -> result.append(v).append(EOL));
		}
		return result.toString();
	}

	static List<Class<?>> toClasses(final String[] args) {
		return Arrays.stream(args).map(LambdaExt.uncheckF(s -> Class.forName(s))).collect(toList());
	}

	public static String paramKey(final Method apiMethod, final int i) {
		return "param-" + apiMethod.getDeclaringClass().getName() + "." + apiMethod.getName() + "-" + i;
	}

	public static String roleKey(final Class<?> clazz) {
		return "role-" + clazz.getName();
	}

	public static String methodKey(final Method method) {
		return "method-" + method.getDeclaringClass().getName() + "." + method.getName();
	}

	public static String methodKey(final Class<?> declaringClass, final String methodName) {
		return "method-" + declaringClass.getName() + "." + methodName;
	}

	public static String valueId(final TestStep step, final int index) {
		return "step_" + step.getOrdinal() + "-val_" + index;
	}

}
