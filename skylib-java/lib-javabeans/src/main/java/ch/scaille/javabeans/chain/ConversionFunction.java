package ch.scaille.javabeans.chain;

import ch.scaille.javabeans.converters.ConversionException;

@FunctionalInterface
interface ConversionFunction {
	Object apply(Object value) throws ConversionException;
}