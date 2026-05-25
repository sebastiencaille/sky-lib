package ch.scaille.javabeans.chain;

import ch.scaille.javabeans.converters.ConversionException;
import org.jspecify.annotations.Nullable;


public interface ConversionFunction<T extends @Nullable Object, R extends @Nullable Object> {
    R apply(T value) throws ConversionException;
}