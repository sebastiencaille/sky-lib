package ch.skymarshall.gui.mvc.persisters;

import ch.skymarshall.gui.mvc.properties.IPersister;

public interface IPersisterFactory<T> {
	IPersister<T> asPersister(final Object object);
}