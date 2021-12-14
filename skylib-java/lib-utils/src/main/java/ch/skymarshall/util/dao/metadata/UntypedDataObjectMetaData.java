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

import java.util.Set;

/**
 * This class contains the meta-data for an arbitrary class.<br>
 * It does not enforce data type on purpose.
 */
public class UntypedDataObjectMetaData extends AbstractObjectMetaData<Object> {

	public UntypedDataObjectMetaData(final Class<?> aclass) {
		super(aclass);
	}

	public UntypedDataObjectMetaData(final Class<?> aclass, final boolean accessPrivateFields) {
		super(aclass, accessPrivateFields);
	}

	public UntypedDataObjectMetaData(final Class<?> dataType, final Set<String> attribNames) {
		super(dataType, attribNames);
	}

	public UntypedDataObjectManager createUntypedObjectAccessorFor(final Object anObject) {
		return new UntypedDataObjectManager(this, anObject);
	}

}
