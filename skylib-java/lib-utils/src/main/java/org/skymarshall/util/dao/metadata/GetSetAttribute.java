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
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * This class allows accessing an attribute through its get/set methods
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class GetSetAttribute<T> extends AbstractAttributeMetaData<T> {

	protected final Method setter;
	protected final Method getter;

	public GetSetAttribute(final String name, final Method getter, final Method setter) {
		super(name, getter.getReturnType());
		this.getter = getter;
		this.setter = setter;
	}

	public Method getSetter() {
		return setter;
	}

	public Method getGetter() {
		return getter;
	}

	@Override
	public Object getValueOf(final T from) {
		try {
			return getter.invoke (from);
		} catch (final Exception e) {
			throw new IllegalStateException("Unable to get object", e);
		}
	}

	@Override
	public void setValueOf(final T to, final Object value) {
		try {
			setter.invoke (to, value);
		} catch (final Exception e) {
			throw new IllegalStateException("Unable to set object", e);
		}
	}

	@Override
	public boolean isReadOnly() {
		return setter == null;
	}

	@Override
	public <A extends Annotation> A getAnnotation(final Class<A> annotation) {
		return getter.getAnnotation(annotation);
	}

	@Override
	public Class<?> getDeclaringType() {
		return setter.getDeclaringClass();
	}

	@Override
	public Type getGenericType() {
		return getter.getGenericReturnType();
	}

	@Override
	public String getCodeName() {
		return getName();
	}

	@Override
	public String toString() {
		return name + '(' + type.getName() + ')';
	}

	@Override
	public int getModifier() {
		return getter.getModifiers();
	}
}
