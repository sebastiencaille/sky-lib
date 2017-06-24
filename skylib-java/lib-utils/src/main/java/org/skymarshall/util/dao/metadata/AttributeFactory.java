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
/*
 * Copyright (c) 2008, Caille Sebastien
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above Copyrightnotice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above Copyrightnotice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of the owner nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE CopyrightHOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE CopyrightOWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.skymarshall.util.dao.metadata;

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

	public static <T> AbstractAttributeMetaData<T> create(final Class<?> _currentClass, final String _property,
			final String _name, final Mode mode) {

		switch (mode) {
		case AUTOMATIC:
			try {
				return createGetSetAttribute(_currentClass, _property, _name);
			} catch (final Exception exc) { // NOSONAR
				final FieldAttribute<T> attribute = createFieldAttribute(_currentClass, _property, _name);
				if (attribute != null) {
					return attribute;
				}
			}
			break;
		case FIELD:
			final FieldAttribute<T> attribute = createFieldAttribute(_currentClass, _property, _name);
			if (attribute != null) {
				return attribute;
			}
			break;
		case GET_SET:
			try {
				return createGetSetAttribute(_currentClass, _property, _name);
			} catch (final NoSuchMethodException e) {
				// ignore
			}
			break;
		default:
			throw new IllegalStateException("Unhandled mode " + mode);
		}
		// throw new
		// IllegalStateException("Unable to find any suitable Attribute handler for "
		// + _name + " of "
		// + _currentClass.getName());
		return null;
	}

	private static <T> AbstractAttributeMetaData<T> createGetSetAttribute(final Class<?> _currentClass,
			final String _property, final String _name) throws NoSuchMethodException {
		Method getter;
		getter = _currentClass.getMethod("get" + _property);

		final Class<?> type = getter.getReturnType();

		Method setter = null;
		try {
			setter = _currentClass.getMethod("set" + _property, type);
			return new GetSetAttribute<T>(_name, getter, setter);

		} catch (final Exception e) { // NOSONAR
			Logger.getAnonymousLogger().finest("No setter for " + _name);
			return new ReadOnlyAttribute<T>(_name, getter);
		}
	}

	private static <T> FieldAttribute<T> createFieldAttribute(final Class<?> _currentClass, final String _property,
			final String _name) {
		try {
			return new FieldAttribute<T>(_name, findField(_currentClass, _property));
		} catch (final NoSuchFieldException e) { // NOSONAR
			Logger.getAnonymousLogger().finest("Cannot access field " + _name);
			return null;
		}
	}

	private static Field findField(final Class<?> _currentClass, final String _property) throws NoSuchFieldException {
		Field field;
		try {
			field = _currentClass.getDeclaredField(_property);
		} catch (final NoSuchFieldException e) { // NOSONAR
			field = _currentClass.getDeclaredField(Character.toLowerCase(_property.charAt(0)) + _property.substring(1));
		}
		return field;
	}
}
