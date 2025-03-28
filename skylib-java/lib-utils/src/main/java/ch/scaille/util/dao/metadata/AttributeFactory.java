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
		AUTOMATIC, GET_SET, FIELD, RECORD
	}

	public static <T, V> IAttributeMetaData<T> create(final Class<? super T> currentClass, final String property,
			final String name, final Mode mode) {

		return switch (mode) {
		case AUTOMATIC: 
			try {
				yield createGetSetAttribute(currentClass, property, name);
			} catch (final Exception exc) { // NOSONAR
				yield AttributeFactory.<T, V>createFieldAttribute(currentClass, property, name);
			}
		case FIELD:
			yield AttributeFactory.<T, V>createFieldAttribute(currentClass, property, name);
		case GET_SET:
			try {
				yield createGetSetAttribute(currentClass, property, name);
			} catch (final NoSuchMethodException e) {
				yield null;
			}
		case RECORD:
			yield createRecordAttribute(currentClass, property, name);
		default:
			throw new IllegalStateException("Unhandled mode " + mode);
		};
	}

	private static <T> IAttributeMetaData<T> createGetSetAttribute(final Class<?> currentClass, final String property,
			final String name) throws NoSuchMethodException {
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

	private static <T> IAttributeMetaData<T> createRecordAttribute(final Class<?> currentClass, final String property,
			final String name) {
		try {
			final var getter = currentClass.getMethod(property);
			final var getterHandler = MethodHandles.lookup().unreflect(getter);
			return new ReadOnlyAttribute<>(name, getter, getterHandler);
		} catch (Exception e) { // NOSONAR
			throw new IllegalStateException("Unable to create handler", e);
		}

	}

	private static <T, V> FieldAttribute<T, V> createFieldAttribute(final Class<? super T> currentClass,
			final String property, final String name) {
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
