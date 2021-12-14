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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ch.skymarshall.util.dao.metadata.AttributeFactory.Mode;

/**
 * Contains all the DO's meta-data.
 * <p>
 *
 *
 * @param <D> a data type
 */
public class AbstractObjectMetaData<D> {

	/**
	 * Object attributes
	 */
	protected final Map<String, AbstractAttributeMetaData<D>> attributes = new HashMap<>();

	/**
	 * Type of the object
	 */
	private final Class<? extends D> dataType;

	/**
	 * Access mode
	 */
	private Mode attributeMode = Mode.AUTOMATIC;

	protected AbstractObjectMetaData(final Class<? extends D> clazz) {
		this(clazz, false);
	}

	protected AbstractObjectMetaData(final Class<? extends D> clazz, final boolean accessPrivateFields) {
		dataType = clazz;
		if (accessPrivateFields) {
			attributeMode = Mode.FIELD;
		}
		introspectClass(clazz, accessPrivateFields);
	}

	protected AbstractObjectMetaData(final Class<? extends D> clazz, final Set<String> attribNames) {
		dataType = clazz;
		createAttributesMetaData(clazz, attribNames);
	}

	public Class<? extends D> getDataType() {
		return dataType;
	}

	public Collection<AbstractAttributeMetaData<D>> getAttributes() {
		return new HashSet<>(attributes.values());
	}

	public DataObjectManager<D> createAccessorTo(final D object) {
		return new DataObjectManager<>(this, object);
	}

	public UntypedDataObjectManager createUntypedAccessorTo(final D object) {
		return new UntypedDataObjectMetaData(dataType).createUntypedObjectAccessorFor(object);
	}

	public UntypedDataObjectMetaData createUntypedMetaData() {
		return new UntypedDataObjectMetaData(dataType);
	}

	protected void introspectClass(final Class<?> clazz, final boolean accessPrivateFields) {

		final Set<String> attribNames = new HashSet<>();

		scanMethods(clazz, attribNames);

		scanFields(clazz, accessPrivateFields, attribNames);

		createAttributesMetaData(clazz, attribNames);
		if (clazz.getSuperclass() != null && !Object.class.equals(clazz.getSuperclass())) {
			introspectClass(clazz.getSuperclass(), accessPrivateFields);
		}

	}

	private void scanFields(final Class<?> clazz, final boolean accessPrivateFields, final Set<String> attribNames) {
		for (final Field field : clazz.getDeclaredFields()) {
			final boolean canAccess = Modifier.isPublic(field.getModifiers()) || accessPrivateFields;
			if (canAccess && !field.getDeclaringClass().equals(Object.class)) {
				attribNames.add(field.getName());
			}
		}
	}

	private void scanMethods(final Class<?> clazz, final Set<String> attribNames) {
		for (final Method method : clazz.getMethods()) {
			final String name = method.getName();
			if (method.getDeclaringClass().equals(Object.class)) {
				continue;
			}
			if (name.startsWith("get") && name.length() > 3 && method.getParameterTypes().length == 0
					&& method.getReturnType() != Void.TYPE) {
				attribNames.add(name.substring(3));
			}
			if (name.startsWith("is") && name.length() > 2 && method.getParameterTypes().length == 0
					&& (method.getReturnType() == Boolean.TYPE || method.getReturnType() == Boolean.class)) {
				attribNames.add(name.substring(2));
			}
		}
	}

	protected void createAttributesMetaData(final Class<?> clazz, final Set<String> attribNames) {
		for (final String name : attribNames) {
			final String attribName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
			final AbstractAttributeMetaData<D> access = AttributeFactory.<D>create(clazz, name, attribName,
					attributeMode);
			if (access != null) {
				attributes.put(attribName, access);
			}
		}
	}

	public void removeAttribute(final String ignoredAttribute) {
		if (attributes.remove(ignoredAttribute) == null) {
			throw new IllegalArgumentException("No such attribute:" + ignoredAttribute);
		}
	}

	public boolean hasAttribute(final String name) {
		return attributes.containsKey(name);
	}

	public AbstractAttributeMetaData<D> getAttribute(final String name) {

		final AbstractAttributeMetaData<D> attrib = attributes.get(name);
		if (attrib == null) {
			throw new IllegalArgumentException("No such attribute:" + name);
		}
		return attrib;
	}

	public void copy(final D from, final D to) {
		for (final AbstractAttributeMetaData<D> attrib : attributes.values()) {
			if (!attrib.isReadOnly()) {
				attrib.copy(from, to);
			}
		}
	}

	public Collection<String> getAttributeNames() {
		return attributes.keySet();
	}

	public String getName() {
		return getDataType().getName();
	}

	public String getSimpleName() {
		return getDataType().getSimpleName();
	}

	public Constructor<? extends D> getConstructor() throws NoSuchMethodException {
		return getDataType().getConstructor();
	}

	@Override
	public String toString() {
		return "MetaData of " + dataType.getName();
	}
}
