package ch.scaille.javabeans.chain;

import ch.scaille.javabeans.converters.ConversionException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface Link<P extends @Nullable Object, C extends @Nullable Object> {

	C toComponent(P value) throws ConversionException;

	P toProperty(Object component, C value) throws ConversionException;

	void unbind();
}