package ch.scaille.javabeans.converters;

import ch.scaille.javabeans.properties.PropertiesContext;
import ch.scaille.javabeans.properties.AbstractProperty;
import org.jspecify.annotations.Nullable;

/**
 * 
 * @param <P> The property side type
 * @param <C> The component side type
 * @param <K> the context type
 */
public interface IConverterWithContext<P extends @Nullable Object, C extends @Nullable Object, K> {

	default void initialize(final AbstractProperty p) {
		// noop
	}

	C convertPropertyValueToComponentValue(P propertyValue, K context);

	P convertComponentValueToPropertyValue(C componentValue, K context) throws ConversionException;
	
	PropertiesContext<K> contextProperties();

}
