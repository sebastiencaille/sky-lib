package ch.scaille.javabeans.converters;

import org.jspecify.annotations.Nullable;

import ch.scaille.javabeans.properties.IProperty;

/**
 * Allows converting property-side values and component-side values.
 * @param <P> The property side type
 * @param <C> The component side type
 */

public interface IConverter<P extends @Nullable Object, C extends @Nullable Object> {

	default void initialize(final IProperty p) {
		// noop
	}

	C convertPropertyValueToComponentValue(P propertyValue);

	P convertComponentValueToPropertyValue(C componentValue) throws ConversionException;

}
