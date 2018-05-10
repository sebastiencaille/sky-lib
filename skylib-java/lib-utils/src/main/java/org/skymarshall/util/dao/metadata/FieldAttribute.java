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
package org.skymarshall.util.dao.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * This class allows to access a public field attribute
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class FieldAttribute<T> extends AbstractAttributeMetaData<T> {

	private final Field field;

	public FieldAttribute(final String name, final Field field) {
		super(name, field.getType());
		this.field = field;
		this.field.setAccessible(true);
	}

	@Override
	public Object getValueOf(final T from) {
		try {
			return field.get(from);
		} catch (final Exception e) {
			throw new IllegalStateException("Unable to get object", e);
		}

	}

	@Override
	public void setValueOf(final T to, final Object value) {
		try {
			field.set(to, value);
		} catch (final Exception e) {
			throw new IllegalStateException("Unable to set object", e);
		}

	}

	@Override
	public boolean isReadOnly() {
		return Modifier.isFinal(field.getModifiers());
	}

	@Override
	public <A extends Annotation> A getAnnotation(final Class<A> annotation) {
		return field.getAnnotation(annotation);
	}

	@Override
	public Class<?> getDeclaringType() {
		return field.getDeclaringClass();
	}

	@Override
	public Type getGenericType() {
		return field.getGenericType();
	}

	@Override
	public String getCodeName() {
		return field.getName();
	}

	@Override
	public String toString() {
		return "Field " + field.getName();
	}

	@Override
	public int getModifier() {
		return field.getModifiers();
	}
}
