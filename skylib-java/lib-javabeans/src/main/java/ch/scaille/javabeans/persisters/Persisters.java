package ch.scaille.javabeans.persisters;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Function;

import ch.scaille.javabeans.persisters.IPersisterFactory.IObjectProvider;
import ch.scaille.javabeans.properties.IPersister;
import ch.scaille.util.dao.metadata.AbstractAttributeMetaData;

public interface Persisters {

	/**
	 * Creates a persister that accesses an object using an IPersisterFactory 
	 * @param <T> the type of the persisted bean
	 * @param <V> the type of the persisted attribute
	 */
	static <T, V> IPersister<V> persister(final IObjectProvider<T> objectProvider,
			final IPersisterFactory<T, V> persisterFactory) {
		return persisterFactory.asPersister(objectProvider);
	}

	/**
	 * Simple object wrapper
	 * @param <T> the type of the persisted bean
	 * @param object the object
	 */
	static <T> IObjectProvider<T> of(T object) {
		return () -> object;
	}

	/**
	 * Creates a persister factory that allows accessing an object using getters/setters
	 * @param <T> the type of the persisted bean
	 * @param <V> the type of the persisted attribute
	 */
	static <T, V> IPersisterFactory<T, V> persister(final Function<T, V> getter, final BiConsumer<T, V> setter) {
		return new GetSetAccess<>(getter, setter);
	}

	/**
	 * Creates a persister factory that allows accessing an object using the metadata package
	 * @param <V> the type of the persisted bean
	 * @param <V> the type of the persisted attribute
	 */
	static <T, V> IPersisterFactory<T, V> persister(AbstractAttributeMetaData<T, V> attribute) {
		return new AttributeMetaDataAccess<>(attribute);
	}

	/**
	 * Creates a persister factory that allows accessing an object through its public fields
	 * @param <T> the type of the persisted bean
	 * @param <V> the type of the persisted attribute
	 */
	static <T, V> IPersisterFactory<T, V> publicField(final Field field) {
		return new MethodHandlerAccess<>(field);
	}

}
