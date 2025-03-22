package ch.scaille.javabeans.chain;

import ch.scaille.javabeans.converters.ConversionException;

public interface ConversionFunction {
	Object apply(Object value) throws ConversionException;
}