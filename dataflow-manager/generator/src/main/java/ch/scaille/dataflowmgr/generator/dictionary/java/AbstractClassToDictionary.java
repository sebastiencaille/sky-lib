package ch.scaille.dataflowmgr.generator.dictionary.java;

import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import ch.scaille.dataflowmgr.annotations.Input;

public abstract class AbstractClassToDictionary {

	protected String returnType(final Method m) {
		return m.getReturnType().getName();
	}

	protected String methodParamName(final Parameter p) {
		final var inputAnnotation = p.getDeclaredAnnotation(Input.class);
		if (inputAnnotation == null) {
			return p.getName();
		}
		return inputAnnotation.value();
	}

	protected String methodParamType(final Parameter p) {
		return p.getParameterizedType().getTypeName().replace('$', '.');
	}

	protected String methodFullName(final Method m) {
		return m.getDeclaringClass().getName() + "." + m.getName();
	}

	protected LinkedHashMap<String, String> parameters(final Method m) {
		return Arrays.stream(m.getParameters())
				.collect(toMap(this::methodParamName, this::methodParamType, (s1, s2) -> s1, LinkedHashMap::new));
	}

	public Stream<Method> methodsOf(final Class<?> clazz) {
		return Stream.of(clazz.getMethods()).filter(m -> !Object.class.equals(m.getDeclaringClass()));
	}

}
