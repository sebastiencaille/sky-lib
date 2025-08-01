package ch.scaille.javabeans.converters;

import ch.scaille.javabeans.properties.AbstractProperty;

/**
 * 
 * @param <P> The property side type
 * @param <C> The component side type
 */
public interface IConverter<P, C> {

	default void initialize(final AbstractProperty p) {
		// noop
	}
	
	C convertPropertyValueToComponentValue(final P propertyValue);

	P convertComponentValueToPropertyValue(C componentValue) throws ConversionException;

}
