package ch.scaille.javabeans.converters;

import org.jspecify.annotations.Nullable;

import ch.scaille.javabeans.properties.IProperty;
import ch.scaille.javabeans.properties.PropertiesContext;

/**
 * 
 * @param <P> The property side type
 * @param <C> The component side type
 * @param <K> the context type
 */
public interface IContextualConverter<P extends @Nullable Object, C extends @Nullable Object, K> {

	default void initialize(final IProperty p) {
		// noop
	}

	C convertPropertyValueToComponentValue(P propertyValue, K context);

	P convertComponentValueToPropertyValue(C componentValue, K context) throws ConversionException;
	
	PropertiesContext<K> contextProperties();

}
