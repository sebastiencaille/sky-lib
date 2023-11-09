package ch.scaille.util.dao.metadata;

import java.lang.reflect.InvocationTargetException;

/**
 * This class gives access to the attributes of a class.
 *
 * @author Sebastien Caille
 *
 * @param <D>
 */
public class DataObjectManager<D> {

	private class ObjectAttributeAccessor implements DataObjectAttribute {

		private final String name;

		public ObjectAttributeAccessor(final String name) {
			this.name = name;
		}

		@Override
		public Object getValue() {
			return DataObjectManager.this.getValueOf(name);
		}

		@Override
		public <T> T getValue(final Class<T> clazz) {
			return DataObjectManager.this.getValueOf(name, clazz);
		}

		@Override
		public void setValue(final Object value) {
			DataObjectManager.this.setValueOf(name, value);
		}

	}

	protected final AbstractObjectMetaData<D> metaData;

	protected final D object;

	public DataObjectManager(final AbstractObjectMetaData<D> objectMetaData, final D object) {
		metaData = objectMetaData;
		this.object = object;
	}

	public AbstractObjectMetaData<D> getMetaData() {
		return metaData;
	}

	public UntypedDataObjectMetaData getUntypedMetaData(final boolean accessPrivateFields) {
		return new UntypedDataObjectMetaData(metaData.getDataType(), accessPrivateFields);
	}

	public UntypedDataObjectManager getUntypedAccessor() {
		return metaData.createUntypedAccessorTo(object);
	}

	public Object getValueOf(final String name) {
		return metaData.getAttribute(name).getValueOf(object);
	}

	public void setValueOf(final String name, final Object o) {
		metaData.getAttribute(name).setValueOf(object, o);
	}

	public <T> T getValueOf(final String name, final Class<T> clazz) {
		return clazz.cast(metaData.getAttribute(name).getValueOf(object));
	}

	public DataObjectAttribute getAttributeAccessor(final String name) {
		if (!metaData.hasAttribute(name)) {
			throw new IllegalArgumentException("No such attribute: " + name);
		}
		return new ObjectAttributeAccessor(name);
	}

	public void copyInto(final D object) {
		metaData.copy(object, object);
	}

	public D createNewObject()
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return metaData.getConstructor().newInstance();
	}

	public D cloneObject() {
		try {
			final var newObject = createNewObject();
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
