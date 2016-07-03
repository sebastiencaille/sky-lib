/*******************************************************************************
 * Copyright (c) 2013 Sebastien Caille.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.mvc.converters;

import java.util.function.Function;

import org.skymarshall.hmi.Utils;
import org.skymarshall.hmi.mvc.HmiErrors.HmiError;
import org.skymarshall.util.FunctionWithException;
import org.skymarshall.util.Lambda;

public final class Converters {

	private Converters() {

	}

	public static <T, C> AbstractObjectConverter<T, C> converter(final Function<T, C> prop2comp,
			final FunctionWithException<C, T, ConversionException> comp2prop) {
		return new AbstractObjectConverter<T, C>() {

			@Override
			protected C convertPropertyValueToComponentValue(final T propertyValue) {
				return prop2comp.apply(propertyValue);
			}

			@Override
			protected T convertComponentValueToPropertyValue(final C componentValue) throws ConversionException {
				return comp2prop.apply(componentValue);
			}

		};
	}

	public static <C> AbstractIntConverter<C> intConverter(final Function<Integer, C> prop2comp,
			final FunctionWithException<C, Integer, ConversionException> comp2prop) {
		return new AbstractIntConverter<C>() {

			@Override
			protected C convertPropertyValueToComponentValue(final Integer propertyValue) {
				return prop2comp.apply(propertyValue);
			}

			@Override
			protected Integer convertComponentValueToPropertyValue(final C componentValue) throws ConversionException {
				return comp2prop.apply(componentValue);
			}

		};
	}

	public static <C> AbstractLongConverter<C> longConverter(final Function<Long, C> prop2comp,
			final FunctionWithException<C, Long, ConversionException> comp2prop) {
		return new AbstractLongConverter<C>() {

			@Override
			protected C convertPropertyValueToComponentValue(final Long propertyValue) {
				return prop2comp.apply(propertyValue);
			}

			@Override
			protected Long convertComponentValueToPropertyValue(final C componentValue) throws ConversionException {
				return comp2prop.apply(componentValue);
			}

		};
	}

	public static <C> AbstractBooleanConverter<C> booleanConverter(final Function<Boolean, C> prop2comp,
			final FunctionWithException<C, Boolean, ConversionException> comp2prop) {
		return new AbstractBooleanConverter<C>() {

			@Override
			protected C convertPropertyValueToComponentValue(final Boolean propertyValue) {
				return prop2comp.apply(propertyValue);
			}

			@Override
			protected Boolean convertComponentValueToPropertyValue(final C componentValue) throws ConversionException {
				return comp2prop.apply(componentValue);
			}

		};
	}

	public static <C> AbstractFloatConverter<C> floatConverter(final Function<Float, C> prop2comp,
			final FunctionWithException<C, Float, ConversionException> comp2prop) {
		return new AbstractFloatConverter<C>() {

			@Override
			protected C convertPropertyValueToComponentValue(final Float propertyValue) {
				return prop2comp.apply(propertyValue);
			}

			@Override
			protected Float convertComponentValueToPropertyValue(final C componentValue) throws ConversionException {
				return comp2prop.apply(componentValue);
			}

		};
	}

	public static AbstractObjectConverter<HmiError, String> hmiErrorToString() {
		return new HmiErrorToStringConverter();
	}

	public static <T> AbstractObjectConverter<T, T> identity() {
		return converter(Function.identity(), Lambda.<T, ConversionException>identity());
	}

	public static AbstractIntConverter<Integer> intIdentity() {
		return intConverter(Function.identity(), Lambda.<Integer, ConversionException>identity());
	}

	public static AbstractLongConverter<Long> longIdentity() {
		return longConverter(Function.identity(), Lambda.<Long, ConversionException>identity());
	}

	public static AbstractBooleanConverter<Boolean> booleanIdentity() {
		return booleanConverter(Function.identity(), Lambda.<Boolean, ConversionException>identity());
	}

	public static AbstractFloatConverter<Float> floatIdentity() {
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

	public static <T> AbstractObjectConverter<String, String> stringToString() {
		return converter((s) -> (s != null && !s.isEmpty()) ? s : null, s -> s != null ? s : "");
	}

	public static AbstractIntConverter<String> intToString() {
		return intConverter(i -> Integer.toString(i), numberToString(Integer::parseInt));
	}

	public static <T, U> AbstractObjectConverter<T, U> readOnly(final Function<T, U> prop2comp) {
		return converter(prop2comp, o -> {
			throw new IllegalStateException("Read only");
		});
	}

	public static <T> AbstractObjectConverter<T, String> objectToString() {
		return readOnly(Object::toString);
	}

	public static <T extends Enum<T>> AbstractObjectConverter<T, String> enumToString(final Class<T> clazz) {
		return new EnumToStringConverter<>(clazz);
	}

	public static <T extends Number> AbstractObjectConverter<T, String> numberToSize() {
		return readOnly(Utils::toSize);
	}

	public static <T> AbstractObjectConverter<T, Boolean> isNotNull() {
		return readOnly((p) -> p != null);
	}
}
