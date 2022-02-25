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
import java.lang.reflect.Type;

/**
 * This class contains the basic methods and attributes used to access DO's
 * attributes
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public abstract class AbstractAttributeMetaData<T> {

	protected final String name;

	protected final Class<?> type;

	/**
	 * Gets the value of the object's attribute represented by this instance
	 * 
	 * @param from
	 * @return the value
	 */
	public abstract Object getValueOf(T from);

	public abstract void setValueOf(T to, Object value);

	public abstract boolean isReadOnly();

	public abstract Class<?> getDeclaringType();

	public abstract <A extends Annotation> A getAnnotation(Class<A> annotation);

	public abstract Type getGenericType();

	public abstract String getCodeName();

	public abstract int getModifier();

	protected AbstractAttributeMetaData(final String name, final Class<?> type) {
		super();
		this.name = name;
		this.type = type;
	}

	public <U> U get(final T from, final Class<U> clazz) {
		return clazz.cast(getValueOf(from));
	}

	public void copy(final T from, final T to) {
		setValueOf(to, getValueOf(from));
	}

	@Override
	public final boolean equals(final Object o) {
		if (!(o instanceof AbstractAttributeMetaData)) {
			return false;
		}
		return name.equals(((AbstractAttributeMetaData<?>) o).name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public String getName() {
		return name;
	}

	/**
	 * Returns the attribute class, preserving the primitive type.
	 * 
	 * @return
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * Returns the attribute class. Primitive type are replaced by non-primitive
	 * types
	 * 
	 * @return
	 */
	public Class<?> getClassType() {
		if (!type.isPrimitive()) {
			return type;
		}
		if (type == Character.TYPE) {
			return Character.class;
		} else if (type == Boolean.TYPE) {
			return Boolean.class;
		} else if (type == Short.TYPE) {
			return Short.class;
		} else if (type == Integer.TYPE) {
			return Integer.class;
		} else if (type == Long.TYPE) {
			return Long.class;
		} else if (type == Float.TYPE) {
			return Float.class;
		} else if (type == Double.TYPE) {
			return Double.class;
		}
		throw new IllegalStateException("Unhandled type: " + type);
	}

}
