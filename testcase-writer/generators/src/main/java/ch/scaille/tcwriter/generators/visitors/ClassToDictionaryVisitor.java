package ch.scaille.tcwriter.generators.visitors;

import static ch.scaille.tcwriter.generators.Helper.methodKey;
import static ch.scaille.tcwriter.generators.Helper.paramKey;
import static ch.scaille.tcwriter.generators.Helper.roleKey;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import ch.scaille.tcwriter.annotations.TCAction;
import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.annotations.TCCheck;
import ch.scaille.tcwriter.annotations.TCRole;
import ch.scaille.tcwriter.generators.model.IdObject;
import ch.scaille.tcwriter.generators.model.testapi.StepClassifier;
import ch.scaille.tcwriter.generators.model.testapi.TestAction;
import ch.scaille.tcwriter.generators.model.testapi.TestActor;
import ch.scaille.tcwriter.generators.model.testapi.TestApiParameter;
import ch.scaille.tcwriter.generators.model.testapi.TestDictionary;
import ch.scaille.tcwriter.generators.model.testapi.TestParameterFactory;
import ch.scaille.tcwriter.generators.model.testapi.TestParameterFactory.ParameterNature;
import ch.scaille.tcwriter.generators.model.testapi.TestRole;
import ch.scaille.tcwriter.tc.TestObjectDescription;

public class ClassToDictionaryVisitor {

	private final Set<Class<?>> unprocessedActors = new HashSet<>();
	private final Set<Class<?>> unprocessedRoles = new HashSet<>();
	private final Set<Class<?>> unprocessedParameterFactoryClasses = new HashSet<>();
	private final Set<Class<?>> processedParameterFactoryClasses = new HashSet<>();

	private final Map<Class<?>, Set<Method>> apiClassIntrospectionCache = new HashMap<>();

	private final TestDictionary dictionary;

	public ClassToDictionaryVisitor(final TestDictionary dictionary) {
		this.dictionary = dictionary;
	}

	public void visit() {
		processEntryPointClasses();
		processParameterFactories();
	}

	private void processEntryPointClasses() {
		for (final Class<?> roleClass : unprocessedRoles) {
			final TCRole roleAnnotation = roleClass.getAnnotation(TCRole.class);
			final TestRole testRole = new TestRole(roleKey(roleClass));
			dictionary.addDescription(testRole, descriptionFrom(roleAnnotation));
			dictionary.getRoles().put(testRole.getId(), testRole);

			final HashSet<Method> roleMethods = new HashSet<>();
			accumulateApiMethods(roleClass, roleMethods);

			for (final Method actionMethod : roleMethods) {
				final String returnType = (actionMethod.getReturnType() != Void.class)
						? actionMethod.getReturnType().getName()
						: null;

				StepClassifier[] classifiers = computeClassifiers(actionMethod);
				final TestAction testAction = new TestAction(methodKey(actionMethod), actionMethod.getName(),
						returnType, classifiers);
				final List<TestApiParameter> roleActionParameters = processParameters(testAction, actionMethod);
				testAction.getParameters().addAll(roleActionParameters);
				testRole.getActions().add(testAction);
			}
		}

		for (final Class<?> unprocessedActor : unprocessedActors) {
			final TCActors actorsAnnotation = unprocessedActor.getAnnotation(TCActors.class);
			for (String actorDef : actorsAnnotation.value()) {
				String[] actorAndSimpleName = actorDef.split("\\|");
				if (actorAndSimpleName.length < 2) {
					throw new IllegalStateException("At least code|role_simple_call_name must be provided");
				}
				String code = actorAndSimpleName[0];
				String simpleClassName = actorAndSimpleName[1];
				String description;
				if (actorAndSimpleName.length > 2) {
					description = actorAndSimpleName[2];
				} else {
					description = code;
				}
				String humanReadable;
				if (actorAndSimpleName.length > 3) {
					humanReadable = actorAndSimpleName[3];
				} else {
					humanReadable = description;
				}
				Optional<TestRole> role = dictionary.getRoles().entrySet().stream()
						.filter(r -> r.getKey().endsWith("." + simpleClassName)).map(Entry::getValue).findFirst();
				if (role.isPresent()) {
					TestActor actor = new TestActor(code, code, role.get());
					dictionary.addActor(actor, new TestObjectDescription(description, humanReadable));
				}
			}
		}
	}

	private StepClassifier[] computeClassifiers(final Method actionMethod) {
		StepClassifier[] classifiers;
		final TCAction actionAnnotation = actionMethod.getAnnotation(TCAction.class);
		final TCCheck checkAnnotation = actionMethod.getAnnotation(TCCheck.class);
		if (checkAnnotation != null) {
			classifiers = new StepClassifier[] { StepClassifier.CHECK };
		} else if (actionAnnotation != null && actionAnnotation.preparationOnly()) {
			classifiers = new StepClassifier[] { StepClassifier.PREPARATION };
		} else if (actionAnnotation != null) {
			classifiers = new StepClassifier[] { StepClassifier.PREPARATION, StepClassifier.ACTION };
		} else {
			classifiers = StepClassifier.values();
		}
		return classifiers;
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
			if (isJavaType(apiClass)) {
				continue;
			}
			// Process the api class
			final HashSet<Method> valueFactoryMethods = new HashSet<>();

			final TCApi tcApi = apiClass.getAnnotation(TCApi.class);
			if (tcApi != null && tcApi.isSelector()) {
				dictionary.addSelectorType(apiClass);
			}

			accumulateApiMethods(apiClass, valueFactoryMethods);
			valueFactoryMethods.removeIf(m -> !Modifier.isStatic(m.getModifiers()));
			if (valueFactoryMethods.isEmpty()) {
				throw new IllegalStateException("No factory found for type " + apiClass.getName());
			}

			for (final Method valueFactoryMethod : valueFactoryMethods) {
				// Process each method of the class

				processValueFactory(valueFactoryMethod);
			}
		}
	}

	private void processValueFactory(final Method valueFactoryMethod) {
		final TestParameterFactory valueFactory = new TestParameterFactory(methodKey(valueFactoryMethod),
				valueFactoryMethod.getDeclaringClass().getSimpleName() + "." + valueFactoryMethod.getName(),
				ParameterNature.TEST_API, valueFactoryMethod.getReturnType().getName());
		processMethodAnnotation(valueFactory, valueFactoryMethod);

		// Add mandatory parameters (parameters of the method)
		valueFactory.getMandatoryParameters().addAll(processParameters(valueFactory, valueFactoryMethod));

		// Add optional parameters: instance methods of the return type
		final HashSet<Method> factoryApiMethods = new HashSet<>();
		accumulateApiMethods(valueFactoryMethod.getReturnType(), factoryApiMethods);
		factoryApiMethods.removeIf(m -> Modifier.isStatic(m.getModifiers()) || m.getParameterCount() > 1);
		for (final Method factoryMethod : factoryApiMethods) {
			String type;
			if (factoryMethod.getParameterTypes().length > 0) {
				type = factoryMethod.getParameterTypes()[0].getName();
			} else {
				type = TestApiParameter.NO_TYPE;
			}
			final TestApiParameter optionalParameter = new TestApiParameter(methodKey(factoryMethod),
					factoryMethod.getName(), type);
			processMethodAnnotation(optionalParameter, factoryMethod);
			valueFactory.getOptionalParameters().add(optionalParameter);
		}

		forEachSuper(valueFactoryMethod.getReturnType(),
				apiClazz -> dictionary.getParameterFactories().put(apiClazz.getName(), valueFactory));
	}

	private void processMethodAnnotation(final IdObject idObject, final Method apiMethod) {
		final TCApi methodAnnotation = apiMethod.getAnnotation(TCApi.class);
		dictionary.addDescription(idObject, descriptionFrom(methodAnnotation));
	}

	private List<TestApiParameter> processParameters(final IdObject methodIdObject, final Method apiMethod) {
		processMethodAnnotation(methodIdObject, apiMethod);

		final Parameter[] methodParameters = apiMethod.getParameters();
		final List<TestApiParameter> processedParameters = new ArrayList<>();
		for (int i = 0; i < methodParameters.length; i++) {
			final Parameter apiMethodParam = methodParameters[i];

			final TCApi apiMethodAnnotation = apiMethodParam.getAnnotation(TCApi.class);
			final Type apiMethodParamType = apiMethodParam.getType();
			final TestApiParameter testObjectParameter = new TestApiParameter(paramKey(apiMethod, i),
					apiMethod.getName() + '-' + i, apiMethodParamType.getTypeName());
			if (apiMethodAnnotation != null) {
				dictionary.addDescription(testObjectParameter, descriptionFrom(apiMethodAnnotation));
			}
			if (apiMethodParamType instanceof Class) {
				unprocessedParameterFactoryClasses.add((Class<?>) apiMethodParamType);
			}
			processedParameters.add(testObjectParameter);
		}
		return processedParameters;
	}

	public void addClass(final Class<?> tcApiClazz) {
		boolean dispatched = false;
		if (isRole(tcApiClazz)) {
			unprocessedRoles.add(tcApiClazz);
			dispatched = true;
		}
		if (isActor(tcApiClazz)) {
			unprocessedActors.add(tcApiClazz);
			dispatched = true;
		}
		if (isTestApi(tcApiClazz)) {
			unprocessedParameterFactoryClasses.add(tcApiClazz);
			dispatched = true;
		}
		if (!dispatched) {
			throw new IllegalStateException(
					"Class " + tcApiClazz.getName() + " must be annotated with @TCRole or @TCApi or @TCActors");
		}

	}

	private boolean isTestApi(final Class<?> tcApiClazz) {
		return tcApiClazz.getAnnotation(TCApi.class) != null;
	}

	private boolean isActor(final Class<?> tcApiClazz) {
		return tcApiClazz.getAnnotation(TCActors.class) != null;
	}

	private boolean isRole(final Class<?> tcApiClazz) {
		return tcApiClazz.getAnnotation(TCRole.class) != null;
	}

	private void accumulateApiMethods(final Class<?> tcClazz, final Set<Method> methods) {
		if (apiClassIntrospectionCache.containsKey(tcClazz)) {
			methods.addAll(apiClassIntrospectionCache.get(tcClazz));
			return;
		}

		forClassAndSuper(tcClazz, new HashSet<>(), apiClazz -> {
			for (final Method apiMethod : tcClazz.getMethods()) {
				final TCApi annotation = apiMethod.getAnnotation(TCApi.class);
				if (annotation != null) {
					methods.add(apiMethod);
				}
			}
		});
		apiClassIntrospectionCache.put(tcClazz, new HashSet<>(methods));
	}

	private void forEachSuper(final Class<?> tcClazz, final Consumer<Class<?>> classHandler) {
		forClassAndSuper(tcClazz, new HashSet<>(), classHandler);
	}

	private void forClassAndSuper(final Class<?> tcClazz, final Set<Class<?>> processed,
			final Consumer<Class<?>> classHandler) {
		if (tcClazz == null || tcClazz == Object.class) {
			return;
		}
		if (isJavaType(tcClazz)) {
			return;
		}
		processed.add(tcClazz);
		classHandler.accept(tcClazz);
		forClassAndSuper(tcClazz.getSuperclass(), processed, classHandler);
		for (final Class<?> apiClassIface : tcClazz.getInterfaces()) {
			forClassAndSuper(apiClassIface, processed, classHandler);
		}
	}

	private TestObjectDescription descriptionFrom(final TCRole tcrole) {
		return new TestObjectDescription(tcrole.description(), tcrole.humanReadable());
	}

	private TestObjectDescription descriptionFrom(final TCApi tcApi) {
		return new TestObjectDescription(tcApi.description(), tcApi.humanReadable());
	}

	private boolean isJavaType(Class<?> clazz) {
		return clazz.isPrimitive() || clazz.getName().startsWith("java.");
	}
}
