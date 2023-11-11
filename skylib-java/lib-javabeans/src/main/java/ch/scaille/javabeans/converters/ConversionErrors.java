package ch.scaille.javabeans.converters;

import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.ConversionError;

public class ConversionErrors {
	private ConversionErrors() {
	}

	public static ConversionError fromException(final AbstractProperty property, final Exception e) {
		return new ConversionError(property, e.getMessage(), e);
	}
}
