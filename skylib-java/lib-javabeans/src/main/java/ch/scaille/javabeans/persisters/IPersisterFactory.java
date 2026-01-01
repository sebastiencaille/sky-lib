package ch.scaille.javabeans.persisters;

import ch.scaille.javabeans.properties.IPersister;
import lombok.Getter;
import lombok.Setter;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * To create a persister factory, which will create a persister on an object
 * 
 * @author scaille
 *
 * @param <T> Type of the container object
 * @param <A> Type of the object's attribute
 */
@NullMarked
public interface IPersisterFactory<T, A extends @Nullable Object> {

	IPersister<A> asPersister(final IObjectProvider<T> object);
	
	interface IObjectProvider<T> {
		T getObject();
	}

	@Getter
	@Setter
	class ObjectHolder<T> implements IObjectProvider<T> {
		private T object;
	}

}