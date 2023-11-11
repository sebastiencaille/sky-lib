package ch.scaille.javabeans.persisters;

import ch.scaille.javabeans.properties.IPersister;

/**
 * Allows to persist a property in an attribute of an object
 *
 * @author scaille
 *
 * @param <T> the object type
 * @param <A> the attribute type
 */
public class ObjectProviderPersister<T, A> implements IPersister<A> {

	public interface IObjectProvider<T> {
		T getObject();
	}

	public static class CurrentObjectProvider<T> implements IObjectProvider<T> {

		private T object;

		@Override
		public T getObject() {
			return object;
		}

		public void setObject(final T object) {
			this.object = object;
		}

	}

	private final IObjectProvider<T> target;
	private final IPersisterFactory<T, A> persisterFactory;

	public ObjectProviderPersister(final IObjectProvider<T> target, IPersisterFactory<T, A> persisterFactory) {
		this.target = target;
		this.persisterFactory = persisterFactory;
	}

	@Override
	public A get() {
		return persister().get();
	}

	@Override
	public void set(final A value) {
		persister().set(value);
	}

	private IPersister<A> persister() {
		final var targetObject = target.getObject();
		if (targetObject == null) {
			throw new IllegalStateException("No target object defined");
		}
		return persisterFactory.asPersister(targetObject);
	}

}
