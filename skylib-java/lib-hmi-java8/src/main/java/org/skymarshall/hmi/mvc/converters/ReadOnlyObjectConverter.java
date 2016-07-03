package org.skymarshall.hmi.mvc.converters;

public abstract class ReadOnlyObjectConverter<T, C> extends
		AbstractObjectConverter<T, C> {
	@Override
	protected T convertComponentValueToPropertyValue(final C componentValue)
			throws ConversionException {
		throw new IllegalStateException("Read only object");
	}
}