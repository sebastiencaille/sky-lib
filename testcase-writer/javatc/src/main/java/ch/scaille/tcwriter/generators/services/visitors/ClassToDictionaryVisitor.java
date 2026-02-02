package ch.scaille.tcwriter.generators.services.visitors;

import static ch.scaille.tcwriter.services.generators.Helper.methodKey;
import static ch.scaille.tcwriter.services.generators.Helper.paramKey;
import static ch.scaille.tcwriter.services.generators.Helper.roleKey;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

import ch.scaille.tcwriter.annotations.TCAction;
import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.annotations.TCApi;
import ch.scaille.tcwriter.annotations.TCCheck;
import ch.scaille.tcwriter.annotations.TCRole;
import ch.scaille.tcwriter.model.IdObject;
import ch.scaille.tcwriter.model.TestObjectDescription;
import ch.scaille.tcwriter.model.dictionary.*;

/**
 * To generate a Dictionary from a Java class
 */
public class ClassToDictionaryVisitor {

	private final Set<Class<?>> unprocessedActors = new HashSet<>();
	private final Set<Class<?>> unprocessedRoles = new HashSet<>();
	private final Set<Class<?>> unprocessedParameterFactoryClasses = new HashSet<>();
	private final Set<Class<?>> processedParameterFactoryClasses = new HashSet<>();

	private final Map<Class<?>, Set<Method>> apiClassIntrospectionCache = new HashMap<>();

	private final TestDictionary dictionary = new TestDictionary();

	public ClassToDictionaryVisitor(String classifier, Class<?>... classes) {
		this.dictionary.setClassifier(classifier);
		this.dictionary.getMetadata().setTransientId(classifier);
		Arrays.stream(classes).forEach(this::addClass);
	}

	public TestDictionary visit() {
		processEntryPointClasses();
		processParameterFactories();
		return dictionary;
	}

	private void processEntryPointClasses() {
		unprocessedRoles.forEach(this::processRoles);
		unprocessedActors.forEach(this::processActors);
	}

	private void processRoles(Class<?> roleClass) {
		final var roleAnnotation = Objects.requireNonNull(roleClass.getAnnotation(TCRole.class));
		final var testRole = new TestRole(roleKey(roleClass), roleClass.getSimpleName());
		dictionary.addDescription(testRole, descriptionFrom(roleAnnotation));
		dictionary.getRoles().put(testRole.getId(), testRole);

		final var roleMethods = new HashSet<Method>();
		accumulateApiMethods(roleClass, roleMethods);

		for (final var actionMethod : roleMethods) {
			final var returnType = (actionMethod.getReturnType() != Void.class) ? actionMethod.getReturnType().getName()
					: null;

			final var classifiers = computeClassifiers(actionMethod);
			final var testAction = new TestAction(methodKey(actionMethod), actionMethod.getName(), returnType,
					classifiers);
			final var roleActionParameters = gatherParameters(testAction, actionMethod);
			testAction.getParameters().addAll(roleActionParameters);
			testRole.getActions().add(testAction);
		}
	}

	private void processActors(Class<?> unprocessedActor) {
		final var actorsAnnotation = Objects.requireNonNull(unprocessedActor.getAnnotation(TCActors.class));
		if (actorsAnnotation.value() == null) {
			throw new IllegalStateException("value must not be null");
		}
		for (var actorDef : actorsAnnotation.value()) {
			final var codeVariable = actorDef.variable();
			final var simpleClassName = actorDef.role().getSimpleName();
			final String description = actorDef.description();
			final String humanReadable = actorDef.humanReadable();

			final var role = dictionary.getRoles().entrySet().stream()
					.filter(r -> r.getKey().endsWith("." + simpleClassName))
					.map(Entry::getValue).findFirst();
			if (role.isPresent()) {
				final var actor = new TestActor(codeVariable, codeVariable, role.get());
				dictionary.addActor(actor, new TestObjectDescription(description, humanReadable));
			}
		}
	}

	private StepClassifier[] computeClassifiers(final Method actionMethod) {
		final var actionAnnotation = actionMethod.getAnnotation(TCAction.class);
		final var checkAnnotation = actionMethod.getAnnotation(TCCheck.class);
		StepClassifier[] classifiers;
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
			final var firstParameterClassIterator = unprocessedParameterFactoryClasses.iterator();
			final var apiClass = firstParameterClassIterator.next();
			firstParameterClassIterator.remove();
			if (processedParameterFactoryClasses.contains(apiClass) || isJavaType(apiClass)) {
				continue;
			}
			processedParameterFactoryClasses.add(apiClass);
			processParameterFactoryClass(apiClass);
		}
	}

	protected void processParameterFactoryClass(Class<?> apiClass) {
		final var tcApi = apiClass.getAnnotation(TCApi.class);
		if (tcApi != null && tcApi.isSelector()) {
			dictionary.addSelectorType(apiClass);
		}

		final var valueFactoryMethods = new HashSet<Method>();
		accumulateApiMethods(apiClass, valueFactoryMethods);
		valueFactoryMethods.removeIf(m -> !Modifier.isStatic(m.getModifiers()));
		if (valueFactoryMethods.isEmpty()) {
			throw new IllegalStateException("No factory found for type " + apiClass.getName());
		}

		for (final var valueFactoryMethod : valueFactoryMethods) {
			// Process each method of the class
			processValueFactory(valueFactoryMethod);
		}

	}

	private void processValueFactory(final Method valueFactoryMethod) {
		final var valueFactory = new TestParameterFactory(methodKey(valueFactoryMethod),
				valueFactoryMethod.getDeclaringClass().getSimpleName() + "." + valueFactoryMethod.getName(),
				ParameterNature.TEST_API, valueFactoryMethod.getReturnType().getName());
		processMethodAnnotation(valueFactory, valueFactoryMethod);

		// Add mandatory parameters (parameters of the method)
		valueFactory.getMandatoryParameters().addAll(gatherParameters(valueFactory, valueFactoryMethod));

		// Add optional parameters: instance methods of the return type
		final var factoryApiMethods = new HashSet<Method>();
		accumulateApiMethods(valueFactoryMethod.getReturnType(), factoryApiMethods);
		factoryApiMethods.removeIf(m -> Modifier.isStatic(m.getModifiers()) || m.getParameterCount() > 1);
		for (final var factoryMethod : factoryApiMethods) {
			final String type;
			if (factoryMethod.getParameterTypes().length > 0) {
				type = factoryMethod.getParameterTypes()[0].getName();
			} else {
				type = TestApiParameter.NO_TYPE;
			}
			final var optionalParameter = new TestApiParameter(methodKey(factoryMethod), factoryMethod.getName(), type);
			processMethodAnnotation(optionalParameter, factoryMethod);
			valueFactory.getOptionalParameters().add(optionalParameter);
		}

		forEachSuper(valueFactoryMethod.getReturnType(),
				apiClazz -> dictionary.getTestObjectFactories().put(apiClazz.getName(), valueFactory));
	}

	private void processMethodAnnotation(final IdObject idObject, final Method apiMethod) {
		final var methodAnnotation = Objects.requireNonNull(apiMethod.getAnnotation(TCApi.class));
		dictionary.addDescription(idObject, descriptionFrom(methodAnnotation));
	}

	private List<TestApiParameter> gatherParameters(final IdObject methodIdObject, final Method apiMethod) {
		processMethodAnnotation(methodIdObject, apiMethod);

		final var methodParameters = apiMethod.getParameters();
		final var processedParameters = new ArrayList<TestApiParameter>();
		for (int i = 0; i < methodParameters.length; i++) {
			final var apiMethodParam = methodParameters[i];

			final var apiMethodAnnotation = apiMethodParam.getAnnotation(TCApi.class);
			final var apiMethodParamType = apiMethodParam.getType();
			final var testObjectParameter = new TestApiParameter(paramKey(apiMethod, i), apiMethod.getName() + '-' + i,
					apiMethodParamType.getTypeName());
			if (apiMethodAnnotation != null) {
				dictionary.addDescription(testObjectParameter, descriptionFrom(apiMethodAnnotation));
			}
			unprocessedParameterFactoryClasses.add(apiMethodParamType);
			processedParameters.add(testObjectParameter);
		}
		return processedParameters;
	}

	public void addClass(final Class<?> tcApiClazz) {
		if (isRole(tcApiClazz)) {
			unprocessedRoles.add(tcApiClazz);
		}
		if (isActor(tcApiClazz)) {
			unprocessedActors.add(tcApiClazz);
		} 
		if (isTestApi(tcApiClazz)) {
			unprocessedParameterFactoryClasses.add(tcApiClazz);
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

		forClassAndSuper(tcClazz, new HashSet<>(), clazz -> {
			for (final var apiMethod : clazz.getMethods()) {
				final var annotation = apiMethod.getAnnotation(TCApi.class);
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
		if (tcClazz == null || isJavaType(tcClazz)) {
			// Only process application classes
			return;
		}
		processed.add(tcClazz);
		classHandler.accept(tcClazz);
		forClassAndSuper(tcClazz.getSuperclass(), processed, classHandler);
		for (final var apiClassIface : tcClazz.getInterfaces()) {
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
