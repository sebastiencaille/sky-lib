package ch.scaille.javabeans.chain;

import ch.scaille.javabeans.converters.ConversionException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface ConversionFunction<T extends @Nullable Object, R extends @Nullable Object> {
    R apply(T value) throws ConversionException;
}