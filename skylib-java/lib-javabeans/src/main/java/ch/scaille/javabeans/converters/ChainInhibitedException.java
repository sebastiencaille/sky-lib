package ch.scaille.javabeans.converters;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class ChainInhibitedException extends ConversionException {

	public ChainInhibitedException(String message) {
		super(message);
	}

}
