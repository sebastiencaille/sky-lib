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
package ch.skymarshall.gui.mvc.factories;

import ch.skymarshall.gui.mvc.persisters.FieldAccess;
import ch.skymarshall.gui.mvc.persisters.GetSetAccess;
import ch.skymarshall.gui.mvc.persisters.ObjectProviderPersister;
import ch.skymarshall.gui.mvc.persisters.ObjectProviderPersister.IObjectProvider;
import ch.skymarshall.gui.mvc.properties.IPersister;

public interface Persisters {

	public static <T> IPersister<T> from(final Object object, final FieldAccess<T> fieldAccess) {
		return fieldAccess.asPersister(object);
	}

	public static <T> IPersister<T> from(final IObjectProvider objectProvider, final FieldAccess<T> fieldAccess) {
		return new ObjectProviderPersister<>(objectProvider, fieldAccess);
	}

	public static <T> IPersister<T> from(final Object object, final GetSetAccess<?, T> getSetAccess) {
		return getSetAccess.asPersister(object);
	}

	public static <T> IPersister<T> from(final IObjectProvider objectProvider, final GetSetAccess<?, T> getSetAccess) {
		return new ObjectProviderPersister<>(objectProvider, getSetAccess);
	}

}
