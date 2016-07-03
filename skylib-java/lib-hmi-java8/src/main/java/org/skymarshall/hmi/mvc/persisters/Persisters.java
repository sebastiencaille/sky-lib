package org.skymarshall.hmi.mvc.persisters;

import org.skymarshall.hmi.mvc.persisters.ObjectProviderPersister.IObjectProvider;
import org.skymarshall.hmi.mvc.properties.IPersister;

public class Persisters {

	public static <T> IPersister<T> from(final Object object, final FieldAccess<T> fieldAccess) {
		return fieldAccess.asPersister(object);
	}

	public static <T> IPersister<T> from(final IObjectProvider objectProvider, final FieldAccess<T> fieldAccess) {
		return new ObjectProviderPersister<>(objectProvider, fieldAccess);
	}
}
