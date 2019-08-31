package ch.skymarshall.tcwriter.generators;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterDefinition;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameterDefinition.ParameterNature;
import ch.skymarshall.tcwriter.generators.model.testapi.TestRole;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;
import ch.skymarshall.tcwriter.generators.model.testcase.TestStep;

public interface Helper {

	final char EOL = '\n';

	static String dumpModel(final TestModel model) {
		final StringBuilder builder = new StringBuilder();
		builder.append(model.toString()).append('\n');
		for (final TestRole actor : model.getRoles().values()) {
			builder.append("  ").append(model.descriptionOf(actor)).append(": ").append(actor).append(EOL);
			actor.getActions().forEach(api -> builder.append("    ").append(model.descriptionOf(api)).append(": ")
					.append(api).append('\n'));

		}
		for (final TestParameterDefinition testObject : model.getParameterFactories().values()) {
			builder.append("  ").append(model.descriptionOf(testObject)).append(": ").append(testObject).append(EOL);
			testObject.getMandatoryParameters().forEach(api -> builder.append("    mandatory: ")
					.append(model.descriptionOf(api)).append(": ").append(api).append(EOL));
			testObject.getOptionalParameters().forEach(api -> builder.append("    optional: ")
					.append(model.descriptionOf(api)).append(": ").append(api).append(EOL));
		}
		return builder.toString();
	}

	static void dumpTestCase(final TestCase testCase) {
		for (final TestStep step : testCase.getSteps()) {
			System.out.println(step.getOrdinal() + ": " + step.getActor().getName() + " " + step.getAction().getName());
			step.getParametersValue().forEach(System.out::println);
		}
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

	static String paramKey(final Method apiMethod, final int i) {
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

	class VerbatimValue {
		private final String display;
		private final String id;
		private final ParameterNature nature;

		public VerbatimValue(final String id, final String display, final ParameterNature nature) {
			super();
			this.display = display;
			this.id = id;
			this.nature = nature;
		}

		@Override
		public int hashCode() {
			return id.hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			return (obj instanceof VerbatimValue) && id == ((VerbatimValue) obj).id;
		}

		@Override
		public String toString() {
			return display;
		}

		public String getId() {
			return id;
		}

		public String getDisplay() {
			return display;
		}

		public ParameterNature getNature() {
			return nature;
		}
	}

	static List<VerbatimValue> toReference(final TestCase tc, final Collection<? extends IdObject> idObjects,
			final ParameterNature nature) {
		return idObjects.stream().map(
				idObject -> new VerbatimValue(idObject.getId(), tc.descriptionOf(idObject).getDescription(), nature))
				.collect(Collectors.toList());
	}

}
