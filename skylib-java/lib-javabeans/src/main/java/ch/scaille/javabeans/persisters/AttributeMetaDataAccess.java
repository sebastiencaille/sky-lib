package ch.scaille.javabeans.persisters;

import ch.scaille.javabeans.properties.IPersister;
import ch.scaille.util.dao.metadata.AbstractAttributeMetaData;

/**
 *
 * @author scaille
 *
 * @param <T> Type of the container object
 * @param <A> Type of the object's attribute
 */
public class AttributeMetaDataAccess<T, A> implements IPersisterFactory<T, A> {

	private final AbstractAttributeMetaData<T> metadata;

	public AttributeMetaDataAccess(AbstractAttributeMetaData<T> metadata) {
		this.metadata = metadata;
	}

	@Override
	public IPersister<A> asPersister(final T object) {
		return new IPersister<>() {
			@Override
			public A get() {
				return (A) metadata.getValueOf(object);
			}

			@Override
			public void set(final A value) {
				metadata.setValueOf(object, value);
			}
		};
	}
}
