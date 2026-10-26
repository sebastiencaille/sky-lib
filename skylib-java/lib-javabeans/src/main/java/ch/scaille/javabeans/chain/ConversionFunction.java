package ch.scaille.javabeans.chain;

import org.jspecify.annotations.Nullable;

import ch.scaille.javabeans.converters.ConversionException;


public interface ConversionFunction<T extends @Nullable Object, R extends @Nullable Object> {
    R apply(T value) throws ConversionException;
}