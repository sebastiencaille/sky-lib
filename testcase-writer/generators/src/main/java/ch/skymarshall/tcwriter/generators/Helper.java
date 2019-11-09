package ch.skymarshall.tcwriter.generators;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.skymarshall.tcwriter.generators.model.testapi.TestRole;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;

public class Helper {

	private Helper() {
	}

	private static final char EOL = '\n';

	static String dumpModel(final TestModel model) {
		final StringBuilder builder = new StringBuilder();
		builder.append(model.toString()).append(EOL);
		for (final TestRole actor : model.getRoles().values()) {
			builder.append("  ").append(model.descriptionOf(actor)).append(": ").append(actor).append(EOL);
			actor.getActions().forEach(api -> builder.append("    ").append(model.descriptionOf(api)).append(": ")
					.append(api).append(EOL));

		}
		for (final TestParameterFactory testObject : model.getParameterFactories().values()) {
			builder.append("  ").append(model.descriptionOf(testObject)).append(": ").append(testObject).append(EOL);
			testObject.getMandatoryParameters().forEach(api -> builder.append("    mandatory: ")
					.append(model.descriptionOf(api)).append(": ").append(api).append(EOL));
			testObject.getOptionalParameters().forEach(api -> builder.append("    optional: ")
					.append(model.descriptionOf(api)).append(": ").append(api).append(EOL));
		}
		return builder.toString();
	}

	static String dumpTestCase(final TestCase testCase) {
		final StringBuilder result = new StringBuilder();
		for (final TestStep step : testCase.getSteps()) {
			result.append(step.getOrdinal()).append(": ").append(step.getActor().getName()).append(" ")
					.append(step.getAction().getName()).append(EOL);
			step.getParametersValue().forEach(v -> result.append(v).append(EOL));
		}
		return result.toString();
	}

	static List<Class<?>> toClasses(final String[] args) {
		return Arrays.stream(args).map(t -> {
			try {
				return Class.forName(t);
			} catch (final ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		}).collect(toList());
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
