package ch.scaille.javabeans.converters;

import ch.scaille.javabeans.properties.AbstractProperty;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * 
 * @param <P> The property side type
 * @param <C> The component side type
 */
@NullMarked
public interface IConverter<P, C> {

	default void initialize(final AbstractProperty p) {
		// noop
	}

	@Nullable
	C convertPropertyValueToComponentValue(@Nullable P propertyValue);

	@Nullable
	P convertComponentValueToPropertyValue(@Nullable C componentValue) throws ConversionException;

}
