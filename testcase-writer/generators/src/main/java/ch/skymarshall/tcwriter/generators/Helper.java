package ch.skymarshall.tcwriter.generators;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.TestModel;
import ch.skymarshall.tcwriter.generators.model.TestObject;
import ch.skymarshall.tcwriter.generators.model.TestRole;

public class Helper {
	public static void dumpModel(final TestModel model) {
		System.out.println(model.toString());
		for (final TestRole actor : model.getRoles().values()) {
			System.out.println("  " + model.getDescriptions().get(actor.getId()) + ": " + actor);
			actor.getApis()
					.forEach(api -> System.out.println("    " + model.getDescriptions().get(api.getId()) + ": " + api));
		}
		for (final TestObject testObject : model.getTestObjects().values()) {
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

		public Reference(final String id, final String display) {
			super();
			this.display = display;
			this.id = id;
		}

		@Override
		public String toString() {
			return display;
		}

		public String getId() {
			return id;
		}

	}

	public static List<Reference> toReference(final TestModel model, final Collection<? extends IdObject> idElements) {
		return idElements.stream()
				.map(idElement -> new Reference(idElement.getId(), model.getDescriptions().get(idElement.getId())))
				.collect(Collectors.toList());
	}

}
