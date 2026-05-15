package ch.scaille.javabeans.chain;

import ch.scaille.javabeans.converters.ConversionException;
import org.jspecify.annotations.Nullable;


public interface Link<P extends @Nullable Object, C extends @Nullable Object> {

	C toComponent(P value) throws ConversionException;

	P toProperty(Object component, C value, boolean force) throws ConversionException;

	void unbind();
}