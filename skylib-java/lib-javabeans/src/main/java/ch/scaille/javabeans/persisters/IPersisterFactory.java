package ch.scaille.javabeans.persisters;

import ch.scaille.javabeans.properties.IPersister;
import lombok.Getter;
import lombok.Setter;

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

	@Getter
	@Setter
	class ObjectHolder<T> implements IObjectProvider<T> {
		private T object;
	}

}