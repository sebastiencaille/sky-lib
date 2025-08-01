package ch.scaille.javabeans.converters;

import ch.scaille.javabeans.properties.ContextProperties;
import ch.scaille.javabeans.properties.AbstractProperty;

/**
 * 
 * @param <P> The property side type
 * @param <C> The component side type
 * @param <K> the context type
 */
public interface IContextualConverter<P, C, K> {

	default void initialize(final AbstractProperty p) {
		// noop
	}
	
	C convertPropertyValueToComponentValue(final P propertyValue, K context);

	P convertComponentValueToPropertyValue(C componentValue, K context) throws ConversionException;
	
	ContextProperties<K> contextProperties();

}
