package ch.scaille.javabeans.chain;

import ch.scaille.javabeans.converters.ConversionException;

public interface Link {
	Object toComponent(Object value) throws ConversionException;

	Object toProperty(Object component, Object value) throws ConversionException;

	void unbind();
}