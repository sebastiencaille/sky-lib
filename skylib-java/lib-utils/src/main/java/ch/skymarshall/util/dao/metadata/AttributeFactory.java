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

package ch.skymarshall.util.dao.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Logger;

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

		final Class<?> type = getter.getReturnType();

		Method setter = null;
		try {
			setter = currentClass.getMethod("set" + property, type);
			return new GetSetAttribute<>(name, getter, setter);

		} catch (final Exception e) { // NOSONAR
			Logger.getAnonymousLogger().finest("No setter for " + name);
			return new ReadOnlyAttribute<>(name, getter);
		}
	}

	private static <T> FieldAttribute<T> createFieldAttribute(final Class<?> currentClass, final String property,
			final String name) {
		try {
			return new FieldAttribute<>(name, findField(currentClass, property));
		} catch (final NoSuchFieldException e) { // NOSONAR
			Logger.getAnonymousLogger().finest("Cannot access field " + name);
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
