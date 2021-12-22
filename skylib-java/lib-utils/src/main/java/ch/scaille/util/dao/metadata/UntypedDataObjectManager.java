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

/**
 * This class allows accessing a data object without enforcing the data types in
 * the methods parameters
 *
 * @param <D>
 */
public class UntypedDataObjectManager extends DataObjectManager<Object> {

	public UntypedDataObjectManager(final AbstractObjectMetaData<Object> objectMetaData, final Object object) {
		super(objectMetaData, object);
	}

	
	@Override
	public void copyInto(final Object object) {

		if (!metaData.getDataType().isAssignableFrom(object.getClass())) {
			throw new IllegalStateException("Parameter of type " + object.getClass().getName() + " is not a subtype of "
					+ metaData.getDataType().getName());
		}

		super.copyInto(metaData.getDataType().cast(object));
	}

}
