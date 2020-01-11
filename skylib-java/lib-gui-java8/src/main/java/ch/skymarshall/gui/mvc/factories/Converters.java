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
package ch.skymarshall.gui.mvc.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ch.skymarshall.gui.Utils;
import ch.skymarshall.gui.mvc.GuiErrors.GuiError;
import ch.skymarshall.gui.mvc.converters.ConversionException;
import ch.skymarshall.gui.mvc.converters.GuiErrorToStringConverter;
import ch.skymarshall.gui.mvc.converters.IConverter;
import ch.skymarshall.gui.mvc.converters.WriteOnlyException;
import ch.skymarshall.util.FunctionWithException;
import ch.skymarshall.util.Lambda;

public final class Converters {

	private Converters() {

	}

	public static <T, C> IConverter<T, C> converter(final Function<T, C> prop2comp,
			final FunctionWithException<C, T, ConversionException> comp2prop) {
		return new IConverter<T, C>() {

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

	public static <T, C> IConverter<List<T>, List<C>> listConverter(final Function<T, C> prop2comp,
			final FunctionWithException<C, T, ConversionException> comp2prop) {
		return new IConverter<List<T>, List<C>>() {

			@Override
			public List<C> convertPropertyValueToComponentValue(final List<T> propertyValue) {
				return propertyValue.stream().map(prop2comp::apply).collect(Collectors.toList());
			}

			@Override
			public List<T> convertComponentValueToPropertyValue(final List<C> componentValue)
					throws ConversionException {
				final List<T> result = new ArrayList<>(componentValue.size());
				for (final C compValue : componentValue) {
					result.add(comp2prop.apply(compValue));
				}
				return result;
			}

		};
	}

	public static <V> IConverter<List<V>, List<V>> filter(final Predicate<V> filter) {
		return new IConverter<List<V>, List<V>>() {
			@Override
			public List<V> convertPropertyValueToComponentValue(final List<V> propertyValue) {
				return propertyValue.stream().filter(filter::test).collect(Collectors.toList());
			}

			@Override
			public List<V> convertComponentValueToPropertyValue(final List<V> componentValue) {
				throw new WriteOnlyException();
			}
		};
	}

	public static <T, C> IConverter<List<T>, List<C>> listConverter(final Function<T, C> prop2comp) {
		return new IConverter<List<T>, List<C>>() {

			@Override
			public List<C> convertPropertyValueToComponentValue(final List<T> propertyValue) {
				return propertyValue.stream().map(prop2comp::apply).collect(Collectors.toList());
			}

			@Override
			public List<T> convertComponentValueToPropertyValue(final List<C> componentValue) {
				throw new WriteOnlyException();
			}

		};
	}

	public static <T, C> IConverter<List<T>, List<C>> listConverter(final IConverter<T, C> prop2comp) {
		return new IConverter<List<T>, List<C>>() {

			@Override
			public List<C> convertPropertyValueToComponentValue(final List<T> propertyValue) {
				return propertyValue.stream().map(prop2comp::convertPropertyValueToComponentValue)
						.collect(Collectors.toList());
			}

			@Override
			public List<T> convertComponentValueToPropertyValue(final List<C> componentValue) {
				throw new WriteOnlyException();
			}

		};
	}

	public static <C> IConverter<Integer, C> intConverter(final IntFunction<C> prop2comp,
			final FunctionWithException<C, Integer, ConversionException> comp2prop) {
		return new IConverter<Integer, C>() {

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

	public static <C> IConverter<Long, C> longConverter(final LongFunction<C> prop2comp,
			final FunctionWithException<C, Long, ConversionException> comp2prop) {
		return new IConverter<Long, C>() {

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

	public static <C> IConverter<Boolean, C> booleanConverter(final Function<Boolean, C> prop2comp,
			final FunctionWithException<C, Boolean, ConversionException> comp2prop) {
		return new IConverter<Boolean, C>() {

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

	public static <C> IConverter<Float, C> floatConverter(final Function<Float, C> prop2comp,
			final FunctionWithException<C, Float, ConversionException> comp2prop) {
		return new IConverter<Float, C>() {

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

	public static IConverter<GuiError, String> guiErrorToString() {
		return new GuiErrorToStringConverter();
	}

	public static <T> IConverter<T, T> identity() {
		return converter(Function.identity(), Lambda.<T, ConversionException>identity());
	}

	public static IConverter<Integer, Integer> intIdentity() {
		return intConverter(i -> i, Lambda.<Integer, ConversionException>identity());
	}

	public static IConverter<Long, Long> longIdentity() {
		return longConverter(l -> l, Lambda.<Long, ConversionException>identity());
	}

	public static IConverter<Boolean, Boolean> booleanIdentity() {
		return booleanConverter(Function.identity(), Lambda.<Boolean, ConversionException>identity());
	}

	public static IConverter<Float, Float> floatIdentity() {
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

	public static IConverter<String, String> stringToString() {
		return converter(s -> (s != null && !s.isEmpty()) ? s : null, s -> s != null ? s : "");
	}

	public static IConverter<Integer, String> intToString() {
		return intConverter(Integer::toString, numberToString(Integer::parseInt)); // NOSONAR
	}

	public static IConverter<Long, String> longToString() {
		return longConverter(Long::toString, numberToString(Long::parseLong)); // NOSONAR
	}

	public static IConverter<Boolean, String> booleanToString() {
		return booleanConverter(b -> Boolean.toString(b), Boolean::parseBoolean); // NOSONAR
	}

	/**
	 * Write only converter
	 * 
	 * @param <T>       type on property side
	 * @param <U>       type on component side
	 * @param prop2comp the function to convert value from property side to
	 *                  component side
	 * @return
	 */
	public static <T, U> IConverter<T, U> wo(final Function<T, U> prop2comp) {
		return converter(prop2comp, o -> {
			throw new WriteOnlyException();
		});
	}

	public static <T> IConverter<T, String> objectToString() {
		return wo(Object::toString);
	}

	public static <T extends Number> IConverter<T, String> numberToSize() {
		return wo(Utils::toSize);
	}

	public static <T> IConverter<T, Boolean> isNotNull() {
		return wo(Objects::nonNull);
	}

	public static <T> IConverter<T, T> noOp(final BiConsumer<Boolean, T> consumer) {
		return new IConverter<T, T>() {

			@Override
			public T convertPropertyValueToComponentValue(final T propertyValue) {
				consumer.accept(true, propertyValue);
				return propertyValue;
			}

			@Override
			public T convertComponentValueToPropertyValue(final T componentValue) {
				consumer.accept(false, componentValue);
				return componentValue;
			}
		};
	}

}
