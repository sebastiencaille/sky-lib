package ch.scaille.javabeans.properties;

public interface IPersister<T> {

	T get();

	void set(T value);

}
