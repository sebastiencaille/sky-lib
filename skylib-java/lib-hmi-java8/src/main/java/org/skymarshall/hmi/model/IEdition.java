package org.skymarshall.hmi.model;

import java.io.Closeable;

public interface IEdition<T> extends Closeable {

	T edited();

	@Override
	void close();

}
