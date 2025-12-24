package ch.scaille.javabeans.persisters;

import ch.scaille.javabeans.properties.IPersister;
import org.jspecify.annotations.NullMarked;

/**
 * To create a persister factory, which will create a persister on an object
 * 
 * @author scaille
 *
 * @param <T>
 */
@NullMarked
public interface IPersisterFactory<T, A> {

	IPersister<A> asPersister(final IObjectProvider<T> object);
	
	interface IObjectProvider<T> {
		T getObject();
	}

	class ObjectHolder<T> implements IObjectProvider<T> {
		private T object;

		@Override
		public T getObject() {
			return object;
		}

		public void setObject(T object) {
			this.object = object;
		}
	}


}