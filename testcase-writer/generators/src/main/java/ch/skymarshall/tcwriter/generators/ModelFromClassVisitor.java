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

import ch.skymarshall.tcwriter.annotations.TCApi;
import ch.skymarshall.tcwriter.annotations.TCRole;
import ch.skymarshall.tcwriter.generators.model.IdObject;
import ch.skymarshall.tcwriter.generators.model.ObjectDescription;
import ch.skymarshall.tcwriter.generators.model.testapi.TestAction;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameter;
import ch.skymarshall.tcwriter.generators.model.testapi.TestRole;
import ch.skymarshall.tcwriter.generators.model.testapi.TestParameter.ParameterNature;

public class ModelFromClassVisitor {

	private final Set<Class<?>> unprocessedActorClasses = new HashSet<>();
	private final Set<Class<?>> unprocessedParameterFactoryClasses = new HashSet<>();
	private final Set<Class<?>> processedParameterFactoryClasses = new HashSet<>();

	private final Map<Class<?>, Set<Method>> apiClassIntrospectionCache = new HashMap<>();

	private final TestModel model;

	public ModelFromClassVisitor(final TestModel model) {
		this.model = model;
	}

	public void visit() {
		processActors();
		processParameterFactories();
	}

	private void processActors() {
		for (final Class<?> roleClass : unprocessedActorClasses) {
			final TestRole testRole = new TestRole(roleKey(roleClass));
			final TCRole roleAnnotation = roleClass.getAnnotation(TCRole.class);
			model.addDescription(testRole, descriptionFrom(roleAnnotation));
			model.getRoles().put(testRole.getId(), testRole);

			final HashSet<Method> roleMethods = new HashSet<>();
			accumulateApiMethods(roleClass, roleMethods);

			for (final Method actionMethod : roleMethods) {
				final String returnType = (actionMethod.getReturnType() != Void.class)
						? actionMethod.getReturnType().getName()
						: null;
				final TestAction testAction = new TestAction(methodKey(actionMethod), actionMethod.getName(),
						returnType);
				final List<TestParameter> roleActionParameters = processParameters(testAction, actionMethod);
				testAction.getParameters().addAll(roleActionParameters);
				testRole.getApis().add(testAction);
			}

		}
	}

	private void processParameterFactories() {
		while (!unprocessedParameterFactoryClasses.isEmpty()) {
			final Iterator<Class<?>> firstElementIterator = unprocessedParameterFactoryClasses.iterator();
			final Class<?> apiClass = firstElementIterator.next();
			firstElementIterator.remove();
			if (processedParameterFactoryClasses.contains(apiClass)) {
				continue;
			}
			processedParameterFactoryClasses.add(apiClass);

			// Process the api class
			final HashSet<Method> parameterFactoryMethods = new HashSet<>();

			final TCApi tcApi = apiClass.getAnnotation(TCApi.class);
			if (tcApi != null && tcApi.isNavigation()) {
				model.addNavigationType(apiClass);
			}

			accumulateApiMethods(apiClass, parameterFactoryMethods);
			parameterFactoryMethods.removeIf(m -> !Modifier.isStatic(m.getModifiers()));

			for (final Method parameterFactoryMethod : parameterFactoryMethods) {
				// Process each method of the class

				final TestParameter testParameter = new TestParameter(methodKey(parameterFactoryMethod),
						parameterFactoryMethod.getDeclaringClass().getSimpleName() + "."
								+ parameterFactoryMethod.getName(),
						ParameterNature.TEST_API_TYPE, parameterFactoryMethod.getReturnType().getName());
				processMethodAnnotation(testParameter, parameterFactoryMethod);

				// Add mandatory parameters (parameters of the method)
				testParameter.getMandatoryParameters().addAll(processParameters(testParameter, parameterFactoryMethod));

				// Add optional parameters: instance methods of the return type
				final HashSet<Method> factoryReturnTypeMethods = new HashSet<>();
				accumulateApiMethods(parameterFactoryMethod.getReturnType(), factoryReturnTypeMethods);
				factoryReturnTypeMethods
						.removeIf(m -> Modifier.isStatic(m.getModifiers()) || m.getParameterCount() != 1);
				for (final Method factoryReturnTypeMethod : factoryReturnTypeMethods) {
					final TestParameter optionalParameter = new TestParameter(methodKey(factoryReturnTypeMethod),
							factoryReturnTypeMethod.getName(), ParameterNature.TEST_API_TYPE,
							factoryReturnTypeMethod.getParameterTypes()[0].getName());
					processMethodAnnotation(optionalParameter, factoryReturnTypeMethod);
					testParameter.getOptionalParameters().add(optionalParameter);
				}

				forEachSuper(parameterFactoryMethod.getReturnType(),
						apiClazz -> model.getParameterFactories().put(apiClazz.getName(), testParameter));
			}
		}
	}

	private void processMethodAnnotation(final IdObject idObject, final Method apiMethod) {
		final TCApi methodAnnotation = apiMethod.getAnnotation(TCApi.class);
		model.addDescription(idObject, descriptionFrom(methodAnnotation));
	}

	private List<TestParameter> processParameters(final IdObject methodIdObject, final Method apiMethod) {
		processMethodAnnotation(methodIdObject, apiMethod);

		final AnnotatedType[] annotatedParameterTypes = apiMethod.getAnnotatedParameterTypes();
		final List<TestParameter> processedParameters = new ArrayList<>();
		for (int i = 0; i < annotatedParameterTypes.length; i++) {
			final AnnotatedType apiMethodParam = annotatedParameterTypes[i];

			final TCApi apiMethodAnnotation = apiMethodParam.getAnnotation(TCApi.class);
			final Type apiMethodParamType = apiMethodParam.getType();
			final TestParameter testObjectParameter = new TestParameter(paramKey(apiMethod, i), apiMethod.getName(),
					ParameterNature.TEST_API_TYPE, apiMethodParamType.getTypeName());
			if (apiMethodAnnotation != null) {
				model.addDescription(testObjectParameter, descriptionFrom(apiMethodAnnotation));
			}
			if (apiMethodParamType instanceof Class) {
				unprocessedParameterFactoryClasses.add((Class<?>) apiMethodParamType);
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
			unprocessedParameterFactoryClasses.add(tcApiClazz);
			return;
		}

		throw new IllegalStateException(
				"Class " + tcApiClazz.getName() + " must have @" + TCRole.class.getSimpleName());
	}

	private boolean isTestObject(final Class<?> tcApiClazz) {
		return tcApiClazz.getAnnotation(TCApi.class) != null;
	}

	private boolean isActor(final Class<?> tcApiClazz) {
		return tcApiClazz.getAnnotation(TCRole.class) != null;
	}

	private void accumulateApiMethods(final Class<?> tcClazz, final Set<Method> methods) {
		if (apiClassIntrospectionCache.containsKey(tcClazz)) {
			methods.addAll(apiClassIntrospectionCache.get(tcClazz));
		}

		forEachSuper(tcClazz, new HashSet<>(), apiClazz -> {
			for (final Method apiMethod : tcClazz.getMethods()) {
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

	private ObjectDescription descriptionFrom(final TCRole tcrole) {
		return new ObjectDescription(tcrole.description(), tcrole.stepSummary());
	}

	private ObjectDescription descriptionFrom(final TCApi tcApi) {
		return new ObjectDescription(tcApi.description(), tcApi.stepSummary());
	}
}
