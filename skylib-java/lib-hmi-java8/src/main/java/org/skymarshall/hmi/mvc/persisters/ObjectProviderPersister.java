package org.skymarshall.hmi.mvc.persisters;

import org.skymarshall.hmi.mvc.properties.IPersister;

/**
 * Allows to persist a property in an attribute of an object
 * 
 * @author scaille
 *
 * @param <T>
 */
public class ObjectProviderPersister<T> implements IPersister<T> {

	public interface IObjectProvider {
		Object getObject();
	}

	public static class CurrentObjectProvider implements IObjectProvider {

		private Object object;

		@Override
		public Object getObject() {
			return object;
		}

		public void setObject(final Object object) {
			this.object = object;
		}

	}

	private final FieldAccess<T> fieldAccess;
	private final IObjectProvider target;

	public ObjectProviderPersister(final IObjectProvider target, final FieldAccess<T> fieldAccess) {
		this.target = target;
		this.fieldAccess = fieldAccess;
	}

	@Override
	public T get() {
		final Object targetObject = target.getObject();
		if (targetObject == null) {
			throw new IllegalStateException("No target object defined");
		}
		return fieldAccess.get(targetObject);
	}

	@Override
	public void set(final T value) {
		final Object targetObject = target.getObject();
		if (targetObject == null) {
			throw new IllegalStateException("No target object defined");
		}
		fieldAccess.set(targetObject, value);
	}

}
