package ch.scaille.javabeans.converters;

import ch.scaille.javabeans.properties.AbstractProperty;

public interface IConverter<P, C> {

	default void initialize(final AbstractProperty p) {
		
	}
	
	C convertPropertyValueToComponentValue(final P propertyValue);

	P convertComponentValueToPropertyValue(C componentValue) throws ConversionException;
	
}
