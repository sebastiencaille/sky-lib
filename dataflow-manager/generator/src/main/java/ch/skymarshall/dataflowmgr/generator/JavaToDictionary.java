package ch.skymarshall.dataflowmgr.generator;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import ch.skymarshall.dataflowmgr.annotations.Conditions;
import ch.skymarshall.dataflowmgr.annotations.ExternalAdapters;
import ch.skymarshall.dataflowmgr.annotations.Input;
import ch.skymarshall.dataflowmgr.annotations.Processors;
import ch.skymarshall.dataflowmgr.model.Condition;
import ch.skymarshall.dataflowmgr.model.Dictionary;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;
import ch.skymarshall.dataflowmgr.model.Processor;
import ch.skymarshall.util.helpers.ClassFinder;
import ch.skymarshall.util.helpers.ClassFinder.Policy;

public class JavaToDictionary {

	private final ClassFinder classFinder = ClassFinder.forApp();

	public Dictionary scan(final String apiClassPackage) throws IOException {
		final Dictionary dictionary = new Dictionary();
		classFinder.addExpectedAnnotation(Processors.class, Policy.CLASS_ONLY);
		classFinder.addExpectedAnnotation(ExternalAdapters.class, Policy.CLASS_ONLY);
		classFinder.addExpectedAnnotation(Conditions.class, Policy.CLASS_ONLY);

		for (final Class<?> clazz : classFinder.collect(apiClassPackage).getResult()) {
			if (clazz.isAnnotationPresent(Processors.class)) {
				streamOf(clazz).forEach(m -> dictionary.processors.add(processorFrom(m)));
			} else if (clazz.isAnnotationPresent(Conditions.class)) {
				streamOf(clazz).forEach(m -> dictionary.conditions.add(conditionFrom(m)));
			} else if (clazz.isAnnotationPresent(ExternalAdapters.class)) {
				streamOf(clazz).forEach(m -> dictionary.externalAdapters.add(adapterFrom(m)));
			}
		}
		return dictionary;
	}

	private Stream<Method> streamOf(final Class<?> clazz) {
		return Stream.of(clazz.getMethods()).filter(m -> !Object.class.equals(m.getDeclaringClass()));
	}

	public static Condition conditionFrom(final Method m) {
		if (!Boolean.TYPE.equals(m.getReturnType())) {
			throw new InvalidParameterException("Condition method must return a boolean:" + m);
		}
		return new Condition(methodFullName(m), m.getName(), parameters(m));
	}

	public static ExternalAdapter adapterFrom(final Method m) {
		return new ExternalAdapter(methodFullName(m), m.getName(), parameters(m), returnType(m));
	}

	public static Processor processorFrom(final Method m) {
		return new Processor(methodFullName(m), m.getName(), parameters(m), returnType(m));
	}

	public static String returnType(final Method m) {
		return m.getReturnType().getName();
	}

	public static String methodParamName(final Parameter p) {
		final Input inputAnnotation = p.getDeclaredAnnotation(Input.class);
		if (inputAnnotation == null) {
			return p.getName();
		}
		return inputAnnotation.value();
	}

	public static String methodParamType(final Parameter p) {
		return p.getParameterizedType().getTypeName().replace('$', '.');
	}

	public static String methodFullName(final Method m) {
		return m.getDeclaringClass().getName() + "." + m.getName();
	}

	public static LinkedHashMap<String, String> parameters(final Method m) {
		return Arrays.stream(m.getParameters()).collect(toMap(JavaToDictionary::methodParamName,
				JavaToDictionary::methodParamType, (s1, s2) -> s1, LinkedHashMap::new));
	}

}
