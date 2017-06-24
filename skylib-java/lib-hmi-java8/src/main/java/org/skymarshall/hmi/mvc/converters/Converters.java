/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.mvc.converters;

import java.util.Objects;
import java.util.function.Function;

import org.skymarshall.hmi.Utils;
import org.skymarshall.hmi.mvc.HmiErrors.HmiError;
import org.skymarshall.util.FunctionWithException;
import org.skymarshall.util.Lambda;

public final class Converters {

	private Converters() {

	}

	public static <T, C> AbstractConverter<T, C> converter(final Function<T, C> prop2comp,
			final FunctionWithException<C, T, ConversionException> comp2prop) {
		return new AbstractConverter<T, C>() {

			@Override
			public C convertPropertyValueToComponentValue(final T propertyValue) {
				return prop2comp.apply(propertyValue);
			}

			@Override
			public T convertComponentValueToPropertyValue(final C componentValue) throws ConversionException {
				return comp2prop.apply(componentValue);
			}

		};
	}

	public static <C> AbstractConverter<Integer, C> intConverter(final Function<Integer, C> prop2comp,
			final FunctionWithException<C, Integer, ConversionException> comp2prop) {
		return new AbstractConverter<Integer, C>() {

			@Override
			public C convertPropertyValueToComponentValue(final Integer propertyValue) {
				return prop2comp.apply(propertyValue);
			}

			@Override
			public Integer convertComponentValueToPropertyValue(final C componentValue) throws ConversionException {
				return comp2prop.apply(componentValue);
			}

		};
	}

	public static <C> AbstractConverter<Long, C> longConverter(final Function<Long, C> prop2comp,
			final FunctionWithException<C, Long, ConversionException> comp2prop) {
		return new AbstractConverter<Long, C>() {

			@Override
			public C convertPropertyValueToComponentValue(final Long propertyValue) {
				return prop2comp.apply(propertyValue);
			}

			@Override
			public Long convertComponentValueToPropertyValue(final C componentValue) throws ConversionException {
				return comp2prop.apply(componentValue);
			}

		};
	}

	public static <C> AbstractConverter<Boolean, C> booleanConverter(final Function<Boolean, C> prop2comp,
			final FunctionWithException<C, Boolean, ConversionException> comp2prop) {
		return new AbstractConverter<Boolean, C>() {

			@Override
			public C convertPropertyValueToComponentValue(final Boolean propertyValue) {
				return prop2comp.apply(propertyValue);
			}

			@Override
			public Boolean convertComponentValueToPropertyValue(final C componentValue) throws ConversionException {
				return comp2prop.apply(componentValue);
			}

		};
	}

	public static <C> AbstractConverter<Float, C> floatConverter(final Function<Float, C> prop2comp,
			final FunctionWithException<C, Float, ConversionException> comp2prop) {
		return new AbstractConverter<Float, C>() {

			@Override
			public C convertPropertyValueToComponentValue(final Float propertyValue) {
				return prop2comp.apply(propertyValue);
			}

			@Override
			public Float convertComponentValueToPropertyValue(final C componentValue) throws ConversionException {
				return comp2prop.apply(componentValue);
			}

		};
	}

	public static AbstractConverter<HmiError, String> hmiErrorToString() {
		return new HmiErrorToStringConverter();
	}

	public static <T> AbstractConverter<T, T> identity() {
		return converter(Function.identity(), Lambda.<T, ConversionException>identity());
	}

	public static AbstractConverter<Integer, Integer> intIdentity() {
		return intConverter(Function.identity(), Lambda.<Integer, ConversionException>identity());
	}

	public static AbstractConverter<Long, Long> longIdentity() {
		return longConverter(Function.identity(), Lambda.<Long, ConversionException>identity());
	}

	public static AbstractConverter<Boolean, Boolean> booleanIdentity() {
		return booleanConverter(Function.identity(), Lambda.<Boolean, ConversionException>identity());
	}

	public static AbstractConverter<Float, Float> floatIdentity() {
		return floatConverter(Function.identity(), Lambda.<Float, ConversionException>identity());
	}

	public static <T extends Number> FunctionWithException<String, T, ConversionException> numberToString(
			final FunctionWithException<String, T, NumberFormatException> converter) {
		return new FunctionWithException<String, T, ConversionException>() {
			@Override
			public T apply(final String componentObject) throws ConversionException {

				if (componentObject == null) {
					throw new ConversionException("Null value is not allowed");
				}
				try {
					return converter.apply(componentObject);
				} catch (final NumberFormatException e) {
					throw new ConversionException("Cannot convert to number", e);
				}
			}
		};
	}

	public static AbstractConverter<String, String> stringToString() {
		return converter(s -> (s != null && !s.isEmpty()) ? s : null, s -> s != null ? s : "");
	}

	public static AbstractConverter<Integer, String> intToString() {
		return intConverter(i -> Integer.toString(i), numberToString(Integer::parseInt)); // NOSONAR
	}

	public static <T, U> AbstractConverter<T, U> writeOnly(final Function<T, U> prop2comp) {
		return converter(prop2comp, o -> {
			throw new IllegalStateException("Write only");
		});
	}

	public static <T> AbstractConverter<T, String> objectToString() {
		return writeOnly(Object::toString);
	}

	public static <T extends Number> AbstractConverter<T, String> numberToSize() {
		return writeOnly(Utils::toSize);
	}

	public static <T> AbstractConverter<T, Boolean> isNotNull() {
		return writeOnly(Objects::nonNull);
	}

}
