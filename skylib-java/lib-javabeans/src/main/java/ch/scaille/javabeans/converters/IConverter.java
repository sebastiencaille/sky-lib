package ch.scaille.javabeans.converters;

import ch.scaille.javabeans.properties.AbstractProperty;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Allows converting property-side values and component-side values.
 * @param <P> The property side type
 * @param <C> The component side type
 */
@NullMarked
public interface IConverter<P extends @Nullable Object, C extends @Nullable Object> {

	default void initialize(final AbstractProperty p) {
		// noop
	}

	C convertPropertyValueToComponentValue(P propertyValue);

	P convertComponentValueToPropertyValue(C componentValue) throws ConversionException;

}
