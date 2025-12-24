package ch.scaille.javabeans.properties;

import org.jspecify.annotations.Nullable;

public interface IPersister<T> {

	@Nullable T get();

	void set(@Nullable T value);

}
