package ch.scaille.javabeans.persisters;

import ch.scaille.javabeans.properties.IPersister;
import lombok.Getter;
import lombok.Setter;

import org.jspecify.annotations.NonNull;

/**
 * To create a persister factory, which will create a persister on an object
 * 
 * @author scaille
 *
 * @param <T> Type of the container object
 * @param <A> Type of the object's attribute
 */
public interface IPersisterFactory<T, A> {

	@NonNull
	IPersister<A> asPersister(@NonNull final IObjectProvider<@NonNull T> object);
	
	interface IObjectProvider<T> {
		@NonNull
		T getObject();
	}

	@Getter
	@Setter
	class ObjectHolder<T> implements IObjectProvider<@NonNull T> {
		private T object;
	}

}