package ch.scaille.javabeans.persisters;

import ch.scaille.javabeans.properties.IPersister;
import ch.scaille.util.dao.metadata.AbstractAttributeMetaData;

/**
 *
 * @author scaille
 *
 * @param <T> Type of the container object
 * @param <V> Type of the object's attribute
 */
public class AttributeMetaDataAccess<T, V> implements IPersisterFactory<T, V> {

	private final AbstractAttributeMetaData<T, V> metadata;

	public AttributeMetaDataAccess(AbstractAttributeMetaData<T, V> metadata) {
		this.metadata = metadata;
	}

	@Override
	public IPersister<V> asPersister(final IObjectProvider<T> objectProvider) {
		return new IPersister<>() {
			@Override
			public V get() {
				return (V) metadata.getValueOf(objectProvider.getObject());
			}

			@Override
			public void set(final V value) {
				metadata.setValueOf(objectProvider.getObject(), value);
			}
		};
	}
}
