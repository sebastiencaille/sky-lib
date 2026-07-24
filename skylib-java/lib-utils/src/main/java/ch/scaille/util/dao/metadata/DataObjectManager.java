package ch.scaille.util.dao.metadata;

import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * This class gives access to the attributes of a class.
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class DataObjectManager<T> {

	private class ObjectAttributeAccessor implements DataObjectAttribute {

		private final String name;

		public ObjectAttributeAccessor(final String name) {
			this.name = name;
		}

		@Override
		@Nullable
		public Object getValue() {
			return DataObjectManager.this.getValueOf(name);
		}

		@Override
		public <V> V getValue(final Class<V> clazz) {
			return DataObjectManager.this.getValueOf(name, clazz);
		}

		@Override
		public void setValue(@Nullable final Object value) {
			DataObjectManager.this.setValueOf(name, value);
		}

	}

	@Getter
    protected final AbstractObjectMetaData<T> metaData;

	protected final T object;

	public DataObjectManager(final AbstractObjectMetaData<T> objectMetaData, final T object) {
		metaData = objectMetaData;
		this.object = object;
	}

    public UntypedDataObjectMetaData getUntypedMetaData(final boolean accessPrivateFields) {
		return new UntypedDataObjectMetaData(metaData.getDataType(), accessPrivateFields);
	}

	public UntypedDataObjectManager getUntypedAccessor() {
		return metaData.createUntypedAccessorTo(object);
	}

	@Nullable
	public Object getValueOf(final String name) {
		return Objects.requireNonNull(metaData.getAttribute(name), () -> "No such attribute: " + name).getValueOf(object);
	}

	public void setValueOf(final String name, @Nullable final Object o) {
		metaData.getAttribute(name).setValueOf(object, o);
	}

	public <V> V getValueOf(final String name, final Class<V> clazz) {
		return clazz.cast(metaData.getAttribute(name).getValueOf(object));
	}

	public DataObjectAttribute getAttributeAccessor(final String name) {
		if (!metaData.hasAttribute(name)) {
			throw new IllegalArgumentException("No such attribute: " + name);
		}
		return new ObjectAttributeAccessor(name);
	}

	public void copyInto(final T object) {
		metaData.copy(object, object);
	}

	public T createNewObject()
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return metaData.getConstructor().newInstance();
	}

	public T cloneObject() {
		try {
			final T newObject = createNewObject();
			for (final var attribute : metaData.attributes.values()) {
				attribute.copy(object, newObject);
			}
			return newObject;
		} catch (final Exception e) {
			throw new IllegalStateException("Cannot clone object", e);
		}
	}

	@Override
	public String toString() {
		return "Accessor on " + object + "(using " + metaData + ')';
	}
}
