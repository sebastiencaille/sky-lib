package ch.scaille.javabeans.converters;

import ch.scaille.javabeans.properties.ConversionError;
import ch.scaille.javabeans.properties.IProperty;


public class ConversionErrors {
	private ConversionErrors() {
	}

	public static ConversionError fromException(final IProperty property, final Exception e) {
		return new ConversionError(property, e.getMessage(), e);
	}
}
