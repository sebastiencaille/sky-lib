package ch.scaille.javabeans.converters;

import ch.scaille.javabeans.properties.ConversionError;

public class ConversionErrorToStringConverter implements IConverter<ConversionError, String> {

	private final String noError;

	public ConversionErrorToStringConverter(String noError) {
		this.noError = noError;
	}

	@Override
	public ConversionError convertComponentValueToPropertyValue(final String text) {
		throw new IllegalStateException("Gui error cannot be created for: " + text);
	}

	@Override
	public String convertPropertyValueToComponentValue(final ConversionError value) {
		if (value == null) {
			return noError;
		}
		final var content = value.getContent();
		if (content instanceof Exception) {
			return ((Exception) content).getLocalizedMessage();
		}
		return String.valueOf(content);
	}

}
