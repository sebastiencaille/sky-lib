package ch.scaille.javabeans.properties;

import org.jspecify.annotations.Nullable;

public interface IPersister<T extends @Nullable Object> {

	T get();

	void set(T value);

}
