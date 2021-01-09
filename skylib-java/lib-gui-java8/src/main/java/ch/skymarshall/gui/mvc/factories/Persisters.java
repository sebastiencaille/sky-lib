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

import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Function;

import ch.skymarshall.gui.mvc.persisters.AttributeMetaDataAccess;
import ch.skymarshall.gui.mvc.persisters.GetSetAccess;
import ch.skymarshall.gui.mvc.persisters.IPersisterFactory;
import ch.skymarshall.gui.mvc.persisters.MethodHandlerAccess;
import ch.skymarshall.gui.mvc.persisters.ObjectProviderPersister;
import ch.skymarshall.gui.mvc.persisters.ObjectProviderPersister.IObjectProvider;
import ch.skymarshall.gui.mvc.properties.IPersister;
import ch.skymarshall.util.dao.metadata.AbstractAttributeMetaData;

public interface Persisters {

	public static <T, A> IPersister<A> persister(final IObjectProvider<T> objectProvider,
			final IPersisterFactory<T, A> persisterFactory) {
		return new ObjectProviderPersister<>(objectProvider, persisterFactory);
	}

	public static <T> IObjectProvider<T> of(T object) {
		return () -> object;
	}

	public static <T, A> IPersisterFactory<T, A> getSet(final Function<T, A> getter, final BiConsumer<T, A> setter) {
		return new GetSetAccess<>(getter, setter);
	}

	public static <T, A> IPersisterFactory<T, A> attribute(AbstractAttributeMetaData<T> attribute) {
		return new AttributeMetaDataAccess<>(attribute);
	}

	public static <T, A> IPersisterFactory<T, A> unsafeFieldAccess(final Field field) {
		field.setAccessible(true);
		return new MethodHandlerAccess<>(field);
	}

	public static <T, A> IPersisterFactory<T, A> fieldAccess(final Field field) {
		return new MethodHandlerAccess<>(field);
	}

}
