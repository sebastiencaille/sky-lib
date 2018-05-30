package ch.skymarshall.tcwriter.generators;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.TestModel;
import ch.skymarshall.tcwriter.generators.model.TestParameter;
import ch.skymarshall.tcwriter.generators.model.TestParameter.ParameterNature;
import ch.skymarshall.tcwriter.generators.model.TestRole;

public class Helper {
	public static void dumpModel(final TestModel model) {
		System.out.println(model.toString());
		for (final TestRole actor : model.getRoles().values()) {
			System.out.println("  " + model.getDescriptions().get(actor.getId()) + ": " + actor);
			actor.getApis()
					.forEach(api -> System.out.println("    " + model.getDescriptions().get(api.getId()) + ": " + api));
		}
		for (final TestParameter testObject : model.getParameterFactories().values()) {
			System.out.println("  " + model.getDescriptions().get(testObject.getId()) + ": " + testObject);
			testObject.getMandatoryParameters().forEach(api -> System.out
					.println("    mandatory: " + model.getDescriptions().get(api.getId()) + ": " + api));
			testObject.getOptionalParameters().forEach(api -> System.out
					.println("    optional: " + model.getDescriptions().get(api.getId()) + ": " + api));
		}
	}

	public static List<Class<?>> toClasses(final String[] args) {
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
		return "actor-" + clazz.getName();
	}

	public static String methodKey(final Method method) {
		return "method-" + method.getDeclaringClass().getName() + "." + method.getName();
	}

	public static class Reference {
		private final String display;
		private final String id;
		private final ParameterNature nature;

		public Reference(final String id, final String display, final ParameterNature nature) {
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
			return (obj instanceof Reference) && id == ((Reference) obj).id;
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

	public static List<Reference> toReference(final TestModel model, final Collection<? extends IdObject> idElements,
			final ParameterNature nature) {
		return idElements.stream().map(
				idElement -> new Reference(idElement.getId(), model.getDescriptions().get(idElement.getId()), nature))
				.collect(Collectors.toList());
	}

}
