package ch.scaille.javabeans.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import ch.scaille.javabeans.properties.ContextProperties;
import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.ConversionError;
import ch.scaille.util.helpers.LambdaExt;
import ch.scaille.util.helpers.LambdaExt.BiFunctionWithException;
import ch.scaille.util.helpers.LambdaExt.FunctionWithException;
import ch.scaille.util.text.FormatterHelper;
import org.jspecify.annotations.NonNull;

public final class Converters {

    private Converters() {
        // noop
    }

    public static @NonNull <P, C> IConverter<P, C> wrap(IConverter<P, C> converter) {
        return new IConverter<>() {

            @Override
            public void initialize(@NonNull AbstractProperty p) {
                converter.initialize(p);
            }

            @Override
            public C convertPropertyValueToComponentValue(P propertyValue) {
                return converter.convertPropertyValueToComponentValue(propertyValue);
            }

            @Override
            public P convertComponentValueToPropertyValue(C componentValue) throws ConversionException {
                return converter.convertComponentValueToPropertyValue(componentValue);
            }
        };
    }

    /**
     * Write only converter
     *
     * @param <T>       type on property side
     * @param <U>       type on component side
     * @param prop2comp the function to convert value from property side to
     *                  component side
     */
    public static @NonNull <T, U> IConverter<T, U> listen(final @NonNull Function<T, U> prop2comp) {
        return converter(prop2comp, o -> {
            throw new WriteOnlyException();
        });
    }

    /**
     * Write only converter
     *
     * @param <T>       type on property side
     * @param <U>       type on component side
     * @param prop2comp the function to convert value from property side to
     *                  component side
     */
    public static @NonNull <T, U, K> IContextualConverter<T, U, K> listen(final @NonNull ContextProperties<K> context,
                                                                 final @NonNull BiFunction<T, @NonNull K, U> prop2comp) {
        return converter(context, prop2comp, (o, k) -> {
            throw new WriteOnlyException();
        });
    }


    public static @NonNull <T, C> IConverter<T, C> converter(final @NonNull Function<T, C> prop2comp,
                                                    final @NonNull FunctionWithException<C, T, ConversionException> comp2prop) {
        return new IConverter<>() {

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

    public static @NonNull <P, C, K> IContextualConverter<P, C, K> converter(final @NonNull ContextProperties<K> context,
                                                                    final BiFunction<P, @NonNull K, C> prop2comp,
                                                                    final BiFunctionWithException<C, @NonNull K, P, ConversionException> comp2prop) {
        return new IContextualConverter<>() {

            @Override
            public C convertPropertyValueToComponentValue(final P propertyValue, K context) {
                return prop2comp.apply(propertyValue, context);
            }

            @Override
            public P convertComponentValueToPropertyValue(final C componentValue, K context) throws ConversionException {
                return comp2prop.apply(componentValue, context);
            }

            @Override
            public @NonNull ContextProperties<K> contextProperties() {
                return context;
            }

        };
    }


    public static @NonNull <T, C> IConverter<List<T>, List<C>> listConverter(final @NonNull Function<T, C> prop2comp,
                                                                    final @NonNull FunctionWithException<C, T, ConversionException> comp2prop) {
        return new IConverter<>() {

            @Override
            public List<C> convertPropertyValueToComponentValue(final List<T> propertyValue) {
                return propertyValue.stream().map(prop2comp).toList();
            }

            @Override
            public List<T> convertComponentValueToPropertyValue(final List<C> componentValue)
                    throws ConversionException {
                final var result = new ArrayList<T>(componentValue.size());
                for (final var compValue : componentValue) {
                    result.add(comp2prop.apply(compValue));
                }
                return result;
            }

        };
    }

    public static @NonNull <V> IConverter<List<V>, List<V>> filter(final @NonNull Predicate<V> filter) {
        return new IConverter<>() {
            @Override
            public List<V> convertPropertyValueToComponentValue(final List<V> propertyValue) {
                return propertyValue.stream().filter(filter).toList();
            }

            @Override
            public List<V> convertComponentValueToPropertyValue(final List<V> componentValue) {
                throw new WriteOnlyException();
            }
        };
    }

    public static @NonNull <T, C> IConverter<List<T>, List<C>> listConverter(final @NonNull Function<T, C> prop2comp) {
        return new IConverter<>() {

            @Override
            public List<C> convertPropertyValueToComponentValue(final List<T> propertyValue) {
                return propertyValue.stream().map(prop2comp).toList();
            }

            @Override
            public List<T> convertComponentValueToPropertyValue(final List<C> componentValue) {
                throw new WriteOnlyException();
            }

        };
    }

    public static @NonNull <T, C> IConverter<List<T>, List<C>> listConverter(final IConverter<T, C> prop2comp) {
        return new IConverter<>() {

            @Override
            public List<C> convertPropertyValueToComponentValue(final List<T> propertyValue) {
                return propertyValue.stream().map(prop2comp::convertPropertyValueToComponentValue)
                        .toList();
            }

            @Override
            public List<T> convertComponentValueToPropertyValue(final List<C> componentValue) {
                throw new WriteOnlyException();
            }

        };
    }

    public static @NonNull <C> IConverter<Integer, C> intConverter(final @NonNull IntFunction<C> prop2comp,
                                                          final @NonNull FunctionWithException<C, @NonNull Integer, ConversionException> comp2prop) {
        return new IConverter<>() {

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

    public static @NonNull <C> IConverter<Long, C> longConverter(final @NonNull LongFunction<C> prop2comp,
                                                        final @NonNull FunctionWithException<C, @NonNull Long, ConversionException> comp2prop) {
        return new IConverter<>() {

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

    public static @NonNull <C> IConverter<Boolean, C> booleanConverter(final @NonNull Function<Boolean, C> prop2comp,
                                                              final @NonNull FunctionWithException<C, @NonNull Boolean, ConversionException> comp2prop) {
        return new IConverter<>() {

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

    public static @NonNull <C> IConverter<Float, C> floatConverter(final @NonNull Function<Float, C> prop2comp,
                                                          final @NonNull FunctionWithException<C, @NonNull Float, ConversionException> comp2prop) {
        return new IConverter<>() {

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

    public static @NonNull IConverter<ConversionError, String> guiErrorToString() {
        return new ConversionErrorToStringConverter("");
    }

    public static @NonNull IConverter<ConversionError, String> guiErrorToString(String noError) {
        return new ConversionErrorToStringConverter(noError);
    }

    public static @NonNull <K, V, U> IConverter<Map<K, V>, U> mapContains(K key, U either, U or) {
        return listen(e -> e.containsKey(key) ? either : or);
    }

    public static @NonNull IConverter<String, String> toSingleLine() {
        return converter(s -> s, s -> s != null ? s.replace('\n', ' ') : null);
    }

    public static @NonNull <T> IConverter<T, T> identity() {
        return converter(Function.identity(), LambdaExt.identity());
    }

    public static @NonNull IConverter<Integer, Integer> intIdentity() {
        return intConverter(i -> i, LambdaExt.identity());
    }

    public static @NonNull IConverter<Long, Long> longIdentity() {
        return longConverter(l -> l, LambdaExt.identity());
    }

    public static @NonNull IConverter<Boolean, Boolean> booleanIdentity() {
        return booleanConverter(Function.identity(), LambdaExt.identity());
    }

    public static @NonNull IConverter<Float, Float> floatIdentity() {
        return floatConverter(Function.identity(), LambdaExt.identity());
    }

    public static @NonNull <T extends Number> FunctionWithException<String, T, ConversionException> numberToString(
            final @NonNull FunctionWithException<String, T, NumberFormatException> converter) {
        return c -> {
            if (c == null) {
                throw new ConversionException("Null value is not allowed");
            }
            try {
                return converter.apply(c);
            } catch (final NumberFormatException e) {
                throw new ConversionException("Cannot convert to number", e);
            }
        };
    }

    public static @NonNull IConverter<String, String> stringToString() {
        return converter(s -> (s != null && !s.isEmpty()) ? s : null, s -> s != null ? s : "");
    }

    public static @NonNull IConverter<Integer, String> intToString() {
        return intConverter(Integer::toString, numberToString(Integer::parseInt)); // NOSONAR
    }

    public static @NonNull IConverter<Long, String> longToString() {
        return longConverter(Long::toString, numberToString(Long::parseLong)); // NOSONAR
    }

    public static @NonNull IConverter<Boolean, String> booleanToString() {
        return booleanConverter(b -> Boolean.toString(b), Boolean::parseBoolean); // NOSONAR
    }

    public static @NonNull <T> IConverter<T, String> objectToString() {
        return listen(Object::toString);
    }

    public static @NonNull <T extends Number> IConverter<T, String> numberToSize() {
        return listen(FormatterHelper::toSize);
    }

    public static @NonNull <T> IConverter<T, Boolean> isNotNull() {
        return listen(Objects::nonNull);
    }

    public static @NonNull <T> IConverter<T, T> noOp(final BiConsumer<Boolean, T> consumer) {
        return new IConverter<>() {

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

    public static @NonNull <C> IConverter<Boolean, C> either(final Supplier<C> either, final Supplier<C> or) {
        return new IConverter<>() {

            @Override
            public C convertPropertyValueToComponentValue(final Boolean propertyValue) {
                if (propertyValue != null && propertyValue) {
                    return either.get();
                }
                return or.get();
            }

            /**
             */
            @Override
            public Boolean convertComponentValueToPropertyValue(final C componentValue) {
                throw new IllegalStateException("Write only converter");
            }

        };
    }

    public static @NonNull <P extends Enum<P>> IConverter<P, Boolean> matches(P enumValue) {
        return new IConverter<>() {

            @Override
            public Boolean convertPropertyValueToComponentValue(final P propertyValue) {
                return propertyValue == enumValue;
            }

            /**
             * @throws ConversionException exception thrown when a conversion error occurs
             */
            @Override
            public P convertComponentValueToPropertyValue(final Boolean componentValue) throws ConversionException {
                if (componentValue == null || !componentValue) {
                    throw new ChainInhibitedException("Component is false");
                }
                return enumValue;
            }

        };
    }

}
