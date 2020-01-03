package ch.skymarshall.gui.model;

public interface IEdition<T> extends AutoCloseable {

	T edited();

	@Override
	void close();

}
