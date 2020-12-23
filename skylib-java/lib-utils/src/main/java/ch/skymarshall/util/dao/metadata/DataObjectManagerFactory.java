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

/**
 * This class allows to create DO's accessors
 *
 * @author Sebastien Caille
 *
 */
public class DataObjectManagerFactory {

	private DataObjectManagerFactory() {
	}

	/*
	 * Creates an Accessor that does not enforce the data types in its methods. The
	 * meta-data are extracted from the data's class.
	 */
	public static <T> UntypedDataObjectManager<?> createFor(final T data) {
		return new UntypedDataObjectMetaData(data.getClass()).createUntypedAccessorTo(data);
	}

	/*
	 * Creates an Accessor that enforce the data types in its methods. The meta-data
	 * are extracted from the class clazz.
	 */
	public static <D> DataObjectManager<D> createFor(final Class<D> clazz, final D data) {
		return new DataObjectMetaData<>(clazz).createAccessorTo(data);
	}
}
