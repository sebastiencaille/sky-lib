/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/

package ch.scaille.util.dao.metadata;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import ch.scaille.util.helpers.Logs;

/**
 * This class creates the appropriated Class that allows accessing to an
 * attribute
 *
 * @author Sebastien Caille
 *
 */
abstract class AttributeFactory {

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
				final FieldAttribute<T> attribute = createFieldAttribute(currentClass, property, name);
				if (attribute != null) {
					return attribute;
				}
			}
			break;
		case FIELD:
			final FieldAttribute<T> attribute = createFieldAttribute(currentClass, property, name);
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
		Class<?> type = getter.getReturnType();

		MethodHandle getterHandler;
		try {
			getterHandler = MethodHandles.lookup().unreflect(getter);
		} catch (final Exception e) { // NOSONAR
			throw new IllegalStateException("Unable to create handler", e);
		}

		try {
			Method setter = currentClass.getMethod("set" + property, type);
			MethodHandle setterHandler = MethodHandles.lookup().unreflect(setter);
			return new GetSetAttribute<>(name, getter, getterHandler, setterHandler);
		} catch (final Exception e) { // NOSONAR
			Logs.of(AttributeFactory.class).finest("No setter for " + name);
			return new ReadOnlyAttribute<>(name, getter, getterHandler);
		}
	}

	private static <T> FieldAttribute<T> createFieldAttribute(final Class<?> currentClass, final String property,
			final String name) {
		try {
			Field field = findField(currentClass, property);
			if (Modifier.isStatic(field.getModifiers())) {
				return null;
			}
			if (Modifier.isPublic(field.getModifiers())) {
				return new NioFieldAttribute<>(name, field);
			}
			return new FieldAttribute<>(name, field);
		} catch (final NoSuchFieldException e) { // NOSONAR
			Logs.of(AttributeFactory.class).finest("Cannot access field " + name);
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
