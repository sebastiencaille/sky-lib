package ch.scaille.javabeans.chain;

import ch.scaille.javabeans.converters.ConversionException;
import org.jspecify.annotations.NonNull;

public interface Link<P, C> {

	C toComponent(P value) throws ConversionException;

	P toProperty(@NonNull Object component, C value) throws ConversionException;

	void unbind();
}