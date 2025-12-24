package ch.scaille.javabeans.chain;

import ch.scaille.javabeans.converters.ConversionException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface Link<P, C> {

	@Nullable
	C toComponent(@Nullable P value) throws ConversionException;

	@Nullable
	P toProperty(Object component, @Nullable C value) throws ConversionException;

	void unbind();
}