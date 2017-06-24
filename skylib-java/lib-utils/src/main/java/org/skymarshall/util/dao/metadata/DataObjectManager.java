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

		public ObjectAttributeAccessor(final String _name) {
			name = _name;
		}

		@Override
		public Object getValue() {
			return DataObjectManager.this.getValueOf(name);
		}

		@Override
		public <T> T getValue(final Class<T> _clazz) {
			return DataObjectManager.this.getValueOf(name, _clazz);
		}

		@Override
		public void setValue(final Object _value) {
			DataObjectManager.this.setValueOf(name, _value);
		}

	}

	protected final AbstractObjectMetaData<DataType> metaData;

	protected final DataType object;

	public DataObjectManager(final AbstractObjectMetaData<DataType> objectMetaData, final DataType _object) {
		metaData = objectMetaData;
		object = _object;
	}

	public AbstractObjectMetaData<DataType> getMetaData() {
		return metaData;
	}

	public UntypedDataObjectMetaData getUntypedMetaData(final boolean accessPrivateFields) {
		return new UntypedDataObjectMetaData(metaData.getDataType(), accessPrivateFields);
	}

	public Object getValueOf(final String _name) {
		return metaData.getAttribute(_name).getValueOf(object);
	}

	public void setValueOf(final String _name, final Object _o) {
		metaData.getAttribute(_name).setValueOf(object, _o);
	}

	public <T> T getValueOf(final String _name, final Class<T> _clazz) {
		return _clazz.cast(metaData.getAttribute(_name).getValueOf(object));
	}

	public DataObjectAttribute getAttributeAccessor(final String _name) {
		if (!metaData.hasAttribute(_name)) {
			throw new IllegalArgumentException("No such attribute: " + _name);
		}
		return new ObjectAttributeAccessor(_name);
	}

	public void copyInto(final DataType _object) {
		metaData.copy(object, _object);
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
