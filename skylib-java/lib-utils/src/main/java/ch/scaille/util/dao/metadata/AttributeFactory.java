package ch.scaille.util.dao.metadata;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

import ch.scaille.util.helpers.Logs;

/**
 * This class creates the appropriated Class that allows accessing to an
 * attribute
 *
 * @author Sebastien Caille
 *
 */
class AttributeFactory {

	private static final Logger LOGGER = Logs.of(AttributeFactory.class);

	private AttributeFactory() {
		// noop
	}
	
	public enum Mode {
		AUTOMATIC, GET_SET, FIELD
	}

	public static <T> AbstractAttributeMetaData<T> create(final Class<?> currentClass, final String property,
			final String name, final Mode mode) {

		switch (mode) {
		case AUTOMATIC:
			try {
				return createGetSetAttribute(currentClass, property, name);
			} catch (final Exception exc) { // NOSONAR
				final var attribute = AttributeFactory.<T>createFieldAttribute(currentClass, property, name);
				if (attribute != null) {
					return attribute;
				}
			}
			break;
		case FIELD:
			final var attribute = AttributeFactory.<T>createFieldAttribute(currentClass, property, name);
			if (attribute != null) {
				return attribute;
			}
			break;
		case GET_SET:
			try {
				return createGetSetAttribute(currentClass, property, name);
			} catch (final NoSuchMethodException e) {
				// ignore
			}
			break;
		default:
			throw new IllegalStateException("Unhandled mode " + mode);
		}
		return null;
	}

	private static <T> AbstractAttributeMetaData<T> createGetSetAttribute(final Class<?> currentClass,
			final String property, final String name) throws NoSuchMethodException {
		Method getter;
		try {
			getter = currentClass.getMethod("get" + property);
		} catch (final NoSuchMethodException e) {
			getter = currentClass.getMethod("is" + property);
		}
		final var type = getter.getReturnType();

		MethodHandle getterHandler;
		try {
			getterHandler = MethodHandles.lookup().unreflect(getter);
		} catch (final Exception e) { // NOSONAR
			throw new IllegalStateException("Unable to create handler", e);
		}

		try {
			final var setter = currentClass.getMethod("set" + property, type);
			final var setterHandler = MethodHandles.lookup().unreflect(setter);
			return new GetSetAttribute<>(name, getter, getterHandler, setterHandler);
		} catch (final Exception e) { // NOSONAR
			LOGGER.finest("No setter for " + name);
			return new ReadOnlyAttribute<>(name, getter, getterHandler);
		}
	}

	private static <T> FieldAttribute<T> createFieldAttribute(final Class<?> currentClass, final String property,
			final String name) {
		try {
			var field = findField(currentClass, property);
			if (Modifier.isStatic(field.getModifiers())) {
				return null;
			}
			if (Modifier.isPublic(field.getModifiers())) {
				return new NioFieldAttribute<>(name, field);
			}
			return new FieldAttribute<>(name, field);
		} catch (final NoSuchFieldException e) { // NOSONAR
			LOGGER.finest("Cannot access field " + name);
			return null;
		}
	}

	private static Field findField(final Class<?> currentClass, final String property) throws NoSuchFieldException {
		Field field;
		try {
			field = currentClass.getDeclaredField(property);
		} catch (final NoSuchFieldException e) { // NOSONAR
			field = currentClass.getDeclaredField(Character.toLowerCase(property.charAt(0)) + property.substring(1));
		}
		return field;
	}
}
