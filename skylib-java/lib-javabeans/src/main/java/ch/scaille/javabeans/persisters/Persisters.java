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
	 * @param <A> the type of the persisted attribute
	 */
	static <T, A> IPersister<A> persister(final IObjectProvider<T> objectProvider,
			final IPersisterFactory<T, A> persisterFactory) {
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
	 * @param <A> the type of the persisted attribute
	 */
	static <T, A> IPersisterFactory<T, A> persister(final Function<T, A> getter, final BiConsumer<T, A> setter) {
		return new GetSetAccess<>(getter, setter);
	}

	/**
	 * Creates a persister factory that allows accessing an object using the metadata package
	 * @param <T> the type of the persisted bean
	 * @param <A> the type of the persisted attribute
	 */
	static <T, A> IPersisterFactory<T, A> persister(AbstractAttributeMetaData<T> attribute) {
		return new AttributeMetaDataAccess<>(attribute);
	}

	/**
	 * Creates a persister factory that allows accessing an object through its public fields
	 * @param <T> the type of the persisted bean
	 * @param <A> the type of the persisted attribute
	 */
	static <T, A> IPersisterFactory<T, A> publicField(final Field field) {
		return new MethodHandlerAccess<>(field);
	}

}
