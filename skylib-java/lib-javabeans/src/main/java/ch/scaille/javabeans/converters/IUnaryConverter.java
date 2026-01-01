package ch.scaille.javabeans.converters;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface IUnaryConverter<T extends @Nullable Object> extends IConverter<T, T> {
	// extended
}
