package ch.scaille.javabeans.chain;

import org.jspecify.annotations.Nullable;

import ch.scaille.javabeans.converters.ConversionException;


public interface Link<P extends @Nullable Object, C extends @Nullable Object> {

	C toComponent(P value) throws ConversionException;

	P toProperty(Object component, C value, boolean force) throws ConversionException;

	void unbind();
}