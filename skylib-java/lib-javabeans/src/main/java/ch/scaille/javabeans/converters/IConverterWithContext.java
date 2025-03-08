package ch.scaille.javabeans.converters;

import ch.scaille.javabeans.properties.AbstractProperty;

public interface IConverterWithContext<P, C, K> {
	
	default void initialize(final AbstractProperty p) {
		// noop
	}
	
	C convertPropertyValueToComponentValue(final P propertyValue, K context);

	P convertComponentValueToPropertyValue(C componentValue, K context) throws ConversionException;


}
