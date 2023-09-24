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

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import ch.scaille.annotations.Persistency;

/**
 * This class allows accessing an attribute through its get/set methods
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class GetSetAttribute<T> extends AbstractAttributeMetaData<T> {

	protected final MethodHandle getter;
	protected final MethodHandle setter;
	protected final Method attributeGetterInfo;

	public GetSetAttribute(final String name, Method attributeGetterInfo, final MethodHandle getter,
			final MethodHandle setter) {
		super(name, getter.type().returnType());
		this.attributeGetterInfo = attributeGetterInfo;
		this.getter = getter;
		this.setter = setter;
	}

	public MethodHandle getGetter() {
		return getter;
	}

	public MethodHandle getSetter() {
		return setter;
	}

	@Override
	public Object getValueOf(final T from) {
		try {
			return getter.bindTo(from).invoke();
		} catch (final Throwable e) {
			throw new IllegalStateException("Unable to get object", e);
		}
	}

	@Override
	public void setValueOf(final T to, final Object value) {
		try {
			setter.bindTo(to).invoke(value);
		} catch (final Throwable e) {
			throw new IllegalStateException("Unable to set object", e);
		}
	}

	@Override
	public boolean isReadOnly() {
		if (setter == null) {
			return true;
		}
		final var persistency = getAnnotation(Persistency.class);
		return persistency != null && persistency.readOnly();
	}

	@Override
	public <A extends Annotation> A getAnnotation(final Class<A> annotation) {
		return attributeGetterInfo.getAnnotation(annotation);
	}

	@Override
	public Class<?> getDeclaringType() {
		return attributeGetterInfo.getDeclaringClass();
	}

	@Override
	public Type getGenericType() {
		return attributeGetterInfo.getGenericReturnType();
	}

	@Override
	public String getCodeName() {
		return getName();
	}

	@Override
	public int getModifier() {
		return attributeGetterInfo.getModifiers();
	}

	@Override
	public String toString() {
		return name + '(' + type.getName() + ')';
	}
}
