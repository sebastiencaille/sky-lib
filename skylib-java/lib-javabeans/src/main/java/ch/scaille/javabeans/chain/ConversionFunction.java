package ch.scaille.javabeans.chain;

import ch.scaille.javabeans.converters.ConversionException;
import org.jspecify.annotations.Nullable;

public interface ConversionFunction<T, R> {
	@Nullable
	R apply(@Nullable T value) throws ConversionException;
}