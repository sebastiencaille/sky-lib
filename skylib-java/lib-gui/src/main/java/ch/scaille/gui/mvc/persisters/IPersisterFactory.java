package ch.scaille.gui.mvc.persisters;

import ch.scaille.gui.mvc.properties.IPersister;

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