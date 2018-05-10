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

import java.lang.reflect.InvocationTargetException;

/**
 * This class gives access to the attributes of a class.
 *
 * @author Sebastien Caille
 *
 * @param <DataType>
 */
public class DataObjectManager<DataType> {

	private class ObjectAttributeAccessor implements DataObjectAttribute {

		private final String name;

		public ObjectAttributeAccessor(final String name) {
			this.name = name;
		}

		@Override
		public Object getValue() {
			return DataObjectManager.this.getValueOf(name);
		}

		@Override
		public <T> T getValue(final Class<T> clazz) {
			return DataObjectManager.this.getValueOf(name, clazz);
		}

		@Override
		public void setValue(final Object value) {
			DataObjectManager.this.setValueOf(name, value);
		}

	}

	protected final AbstractObjectMetaData<DataType> metaData;

	protected final DataType object;

	public DataObjectManager(final AbstractObjectMetaData<DataType> objectMetaData, final DataType object) {
		metaData = objectMetaData;
		this.object = object;
	}

	public AbstractObjectMetaData<DataType> getMetaData() {
		return metaData;
	}

	public UntypedDataObjectMetaData getUntypedMetaData(final boolean accessPrivateFields) {
		return new UntypedDataObjectMetaData(metaData.getDataType(), accessPrivateFields);
	}

	public Object getValueOf(final String name) {
		return metaData.getAttribute(name).getValueOf(object);
	}

	public void setValueOf(final String name, final Object o) {
		metaData.getAttribute(name).setValueOf(object, o);
	}

	public <T> T getValueOf(final String name, final Class<T> clazz) {
		return clazz.cast(metaData.getAttribute(name).getValueOf(object));
	}

	public DataObjectAttribute getAttributeAccessor(final String name) {
		if (!metaData.hasAttribute(name)) {
			throw new IllegalArgumentException("No such attribute: " + name);
		}
		return new ObjectAttributeAccessor(name);
	}

	public void copyInto(final DataType object) {
		metaData.copy(object, object);
	}

	public UntypedDataObjectManager<?> getUntypedAccessor() {
		return metaData.createUntypedAccessorTo(object);
	}

	public DataType createNewObject() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		return metaData.getConstructor().newInstance();
	}

	public DataType cloneObject() {
		try {
			final DataType newObject = createNewObject();
			for (final AbstractAttributeMetaData<DataType> attribute : metaData.attributes.values()) {
				attribute.copy(object, newObject);
			}
			return newObject;
		} catch (final Exception e) {
			throw new IllegalStateException("Cannot clone object", e);
		}
	}

	@Override
	public String toString() {
		return "Accessor on " + object + "(using " + metaData + ')';
	}
}
