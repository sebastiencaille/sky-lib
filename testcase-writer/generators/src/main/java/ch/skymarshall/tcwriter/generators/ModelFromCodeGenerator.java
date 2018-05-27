package ch.skymarshall.tcwriter.generators;

import static ch.skymarshall.tcwriter.generators.Helper.methodKey;
import static ch.skymarshall.tcwriter.generators.Helper.paramKey;
import static ch.skymarshall.tcwriter.generators.Helper.roleKey;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import ch.skymarshall.tcwriter.annotations.TCActor;
import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.generators.model.TestMethod;
import ch.skymarshall.tcwriter.generators.model.TestModel;
import ch.skymarshall.tcwriter.generators.model.TestObject;
import ch.skymarshall.tcwriter.generators.model.TestObjectParameter;
import ch.skymarshall.tcwriter.generators.model.TestRole;

public class ModelFromCodeGenerator {

	private final Set<Class<?>> unprocessedActorClasses = new HashSet<>();
	private final Set<Class<?>> unprocessedApiClasses = new HashSet<>();
	private final Set<Class<?>> processedApiClasses = new HashSet<>();

	private final Map<Class<?>, Set<Method>> apiClassIntrospectionCache = new HashMap<>();

	private final TestModel model;

	public ModelFromCodeGenerator(final TestModel model) {
		this.model = model;
	}

	public void visit() {
		processActors();
		processApis();
	}

	private void processActors() {
		for (final Class<?> actorClass : unprocessedActorClasses) {
			final TestRole testActor = new TestRole(roleKey(actorClass));
			final TCActor actorAnnotation = actorClass.getAnnotation(TCActor.class);
			model.getDescriptions().put(roleKey(actorClass), actorAnnotation.description());
			model.getRoles().put(testActor.getId(), testActor);

			final HashSet<Method> apiMethods = new HashSet<>();
			accumulateApiMethods(actorClass, apiMethods);

			for (final Method apiMethod : apiMethods) {
				final TestMethod testMethod = new TestMethod(methodKey(apiMethod), apiMethod.getName());
				final List<TestObjectParameter> processParameters = processParameters(apiMethod);
				testMethod.getParameters().addAll(processParameters);
				testActor.getApis().add(testMethod);
			}

		}
	}

	private void processApis() {
		while (!unprocessedApiClasses.isEmpty()) {
			final Iterator<Class<?>> firstElementIterator = unprocessedApiClasses.iterator();
			final Class<?> tcClazz = firstElementIterator.next();
			firstElementIterator.remove();
			if (processedApiClasses.contains(tcClazz)) {
				continue;
			}
			processedApiClasses.add(tcClazz);

			final HashSet<Method> apiMethods = new HashSet<>();
			accumulateApiMethods(tcClazz, apiMethods);
			apiMethods.removeIf(m -> !Modifier.isStatic(m.getModifiers()));
			for (final Method apiMethod : apiMethods) {

				processMethodAnnotation(apiMethod);
				final TestObject testObject = new TestObject(methodKey(apiMethod),
						apiMethod.getDeclaringClass().getSimpleName() + "." + apiMethod.getName(),
						apiMethod.getReturnType().getName());
				testObject.getMandatoryParameters().addAll(processParameters(apiMethod));

				// Add optional parameters: non static methods of the return type
				final HashSet<Method> returnTypeApiMethods = new HashSet<>();
				accumulateApiMethods(apiMethod.getReturnType(), returnTypeApiMethods);
				returnTypeApiMethods.removeIf(m -> Modifier.isStatic(m.getModifiers()) || m.getParameterCount() != 1);
				for (final Method returnTypeApiMethod : returnTypeApiMethods) {
					processMethodAnnotation(returnTypeApiMethod);
					final TestObjectParameter optionalParameter = new TestObjectParameter(
							methodKey(returnTypeApiMethod), returnTypeApiMethod.getParameterTypes()[0].getName());
					testObject.getOptionalParameters().add(optionalParameter);
				}

				forEachSuper(apiMethod.getReturnType(),
						apiClazz -> model.getTestObjects().put(apiClazz.getName(), testObject));
			}
		}
	}

	private void processMethodAnnotation(final Method apiMethod) {
		final TCApi methodAnnotation = apiMethod.getAnnotation(TCApi.class);
		model.getDescriptions().put(methodKey(apiMethod), methodAnnotation.description());
	}

	private List<TestObjectParameter> processParameters(final Method apiMethod) {
		processMethodAnnotation(apiMethod);

		final AnnotatedType[] annotatedParameterTypes = apiMethod.getAnnotatedParameterTypes();
		final List<TestObjectParameter> processedParameters = new ArrayList<>();
		for (int i = 0; i < annotatedParameterTypes.length; i++) {
			final AnnotatedType apiMethodParam = annotatedParameterTypes[i];

			final TCApi apiMethodAnnotation = apiMethodParam.getAnnotation(TCApi.class);
			if (apiMethodAnnotation != null) {
				model.getDescriptions().put(paramKey(apiMethod, i), apiMethodAnnotation.description());
			}
			final Type apiMethodParamType = apiMethodParam.getType();
			final TestObjectParameter testObjectParameter = new TestObjectParameter(paramKey(apiMethod, i),
					apiMethodParamType.getTypeName());
			if (apiMethodParamType instanceof Class) {
				unprocessedApiClasses.add((Class<?>) apiMethodParamType);
			}
			processedParameters.add(testObjectParameter);
		}
		return processedParameters;
	}

	public void addClass(final Class<?> tcApiClazz) {
		if (isActor(tcApiClazz)) {
			unprocessedActorClasses.add(tcApiClazz);
			return;
		}
		if (isTestObject(tcApiClazz)) {
			unprocessedApiClasses.add(tcApiClazz);
			return;
		}

		throw new IllegalStateException(
				"Class " + tcApiClazz.getName() + " must have @" + TCActor.class.getSimpleName());
	}

	private boolean isTestObject(final Class<?> tcApiClazz) {
		return tcApiClazz.getAnnotation(TCApi.class) != null;
	}

	private boolean isActor(final Class<?> tcApiClazz) {
		return tcApiClazz.getAnnotation(TCActor.class) != null;
	}

	private void accumulateApiMethods(final Class<?> tcClazz, final Set<Method> methods) {
		if (apiClassIntrospectionCache.containsKey(tcClazz)) {
			methods.addAll(apiClassIntrospectionCache.get(tcClazz));
		}

		forEachSuper(tcClazz, new HashSet<>(),

				apiClazz -> {
					for (

				final Method apiMethod : tcClazz.getMethods()) {
						final TCApi annotation = apiMethod.getAnnotation(TCApi.class);
						if (annotation != null) {
							methods.add(apiMethod);
						}
					}
				});
		apiClassIntrospectionCache.put(tcClazz, methods);
	}

	private void forEachSuper(final Class<?> tcClazz, final Consumer<Class<?>> classHandler) {
		forEachSuper(tcClazz, new HashSet<>(), classHandler);
	}

	private void forEachSuper(final Class<?> tcClazz, final Set<Class<?>> processed,
			final Consumer<Class<?>> classHandler) {
		if (tcClazz == Object.class) {
			return;
		}
		if (!isActor(tcClazz) && !isTestObject(tcClazz)) {
			return;
		}
		processed.add(tcClazz);
		classHandler.accept(tcClazz);
		forEachSuper(tcClazz.getSuperclass(), processed, classHandler);
		for (final Class<?> apiClassIface : tcClazz.getInterfaces()) {
			forEachSuper(apiClassIface, processed, classHandler);
		}
	}

}
