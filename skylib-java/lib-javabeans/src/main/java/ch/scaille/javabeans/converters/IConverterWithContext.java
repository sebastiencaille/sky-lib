package ch.scaille.javabeans.converters;

import ch.scaille.javabeans.properties.PropertiesContext;
import ch.scaille.javabeans.properties.AbstractProperty;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * 
 * @param <P> The property side type
 * @param <C> The component side type
 * @param <K> the context type
 */
@NullMarked
public interface IConverterWithContext<P, C, K> {

	default void initialize(final AbstractProperty p) {
		// noop
	}

	@Nullable
	C convertPropertyValueToComponentValue(@Nullable P propertyValue, K context);

	@Nullable
	P convertComponentValueToPropertyValue(@Nullable C componentValue, K context) throws ConversionException;
	
	PropertiesContext<K> contextProperties();

}
