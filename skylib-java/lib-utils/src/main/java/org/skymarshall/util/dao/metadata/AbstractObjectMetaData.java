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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.skymarshall.util.dao.metadata.AttributeFactory.Mode;

/**
 * Contains all the DO's meta-data.
 * <p>
 *
 *
 * @param <DataType>
 *            a data type
 */
public class AbstractObjectMetaData<DataType> {

	/**
	 * Object attributes
	 */
	protected final Map<String, AbstractAttributeMetaData<DataType>> attributes = new HashMap<>();

	/**
	 * Type of the object
	 */
	private final Class<? extends DataType> dataType;

	/**
	 * Access mode
	 */
	private Mode attributeMode = Mode.AUTOMATIC;

	protected AbstractObjectMetaData(final Class<? extends DataType> _clazz) {
		this(_clazz, false);
	}

	protected AbstractObjectMetaData(final Class<? extends DataType> _clazz, final boolean accessPrivateFields) {
		dataType = _clazz;
		if (accessPrivateFields) {
			attributeMode = Mode.FIELD;
		}
		introspectClass(_clazz, accessPrivateFields);
	}

	protected AbstractObjectMetaData(final Class<? extends DataType> _clazz, final Set<String> _attribNames) {
		dataType = _clazz;
		createAttributesMetaData(_clazz, _attribNames);
	}

	public Class<? extends DataType> getDataType() {
		return dataType;
	}

	public Collection<AbstractAttributeMetaData<DataType>> getAttributes() {
		return new HashSet<>(attributes.values());
	}

	public DataObjectManager<DataType> createAccessorTo(final DataType _object) {
		return new DataObjectManager<>(this, _object);
	}

	public UntypedDataObjectManager<?> createUntypedAccessorTo(final DataType _object) {
		return new UntypedDataObjectManager<>(this, _object);
	}

	public UntypedDataObjectMetaData createUntypedMetaData() {
		return new UntypedDataObjectMetaData(dataType, attributes.keySet());
	}

	protected void introspectClass(final Class<?> _clazz, final boolean accessPrivateFields) {

		final Set<String> attribNames = new HashSet<>();

		for (final Method method : _clazz.getMethods()) {
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

		for (final Field field : _clazz.getDeclaredFields()) {
			final boolean canAccess = Modifier.isPublic(field.getModifiers()) || accessPrivateFields;
			if (canAccess && !field.getDeclaringClass().equals(Object.class)) {
				attribNames.add(field.getName());
			}
		}

		createAttributesMetaData(_clazz, attribNames);
		if (_clazz.getSuperclass() != null && !Object.class.equals(_clazz.getSuperclass())) {
			introspectClass(_clazz.getSuperclass(), accessPrivateFields);
		}

	}

	protected void createAttributesMetaData(final Class<?> _clazz, final Set<String> attribNames) {
		for (final String name : attribNames) {
			final String attribName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
			final AbstractAttributeMetaData<DataType> access = AttributeFactory.<DataType>create(_clazz, name,
					attribName, attributeMode);
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

	public boolean hasAttribute(final String _name) {
		return attributes.containsKey(_name);
	}

	public AbstractAttributeMetaData<DataType> getAttribute(final String _name) {

		final AbstractAttributeMetaData<DataType> attrib = attributes.get(_name);
		if (attrib == null) {
			throw new IllegalArgumentException("No such attribute:" + _name);
		}
		return attrib;
	}

	public void copy(final DataType _from, final DataType _to) {
		for (final AbstractAttributeMetaData<DataType> attrib : attributes.values()) {
			if (!attrib.isReadOnly()) {
				attrib.copy(_from, _to);
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

	@Override
	public String toString() {
		return "MetaData of " + dataType.getName();
	}

	public Constructor<? extends DataType> getConstructor() throws NoSuchMethodException, SecurityException {
		return getDataType().getConstructor();
	}
}
