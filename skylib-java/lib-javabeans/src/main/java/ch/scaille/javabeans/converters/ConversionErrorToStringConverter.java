package ch.scaille.javabeans.converters;

import ch.scaille.javabeans.properties.ConversionError;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ConversionErrorToStringConverter implements IConverter<ConversionError, String> {

	private final String noError;

	public ConversionErrorToStringConverter(String noError) {
		this.noError = noError;
	}

	@Override
	public ConversionError convertComponentValueToPropertyValue(@Nullable final String text) {
		throw new IllegalStateException("Conversion error cannot be created for: " + text);
	}

	@Override
	public String convertPropertyValueToComponentValue(@Nullable final ConversionError value) {
		if (value == null) {
			return noError;
		}
		final var content = value.content();
		if (content instanceof Exception exc) {
			return exc.getLocalizedMessage();
		}
		return String.valueOf(content);
	}

}
