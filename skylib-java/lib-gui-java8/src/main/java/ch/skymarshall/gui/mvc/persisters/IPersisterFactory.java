package ch.skymarshall.gui.mvc.persisters;

import ch.skymarshall.gui.mvc.properties.IPersister;

/**
 * To create a persister factory, which will create a persister on an object
 * 
 * @author scaille
 *
 * @param <T>
 */
public interface IPersisterFactory<T, A> {
	
	IPersister<A> asPersister(final T object);
	
}