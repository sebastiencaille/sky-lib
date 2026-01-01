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

import ch.scaille.javabeans.properties.PropertiesContext;
import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.ConversionError;
import ch.scaille.util.helpers.LambdaExt;
import ch.scaille.util.helpers.LambdaExt.BiFunctionWithException;
import ch.scaille.util.helpers.LambdaExt.FunctionWithException;
import ch.scaille.util.text.FormatterHelper;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class Converters {

    private Converters() {
        // identity
    }

    public static <P extends @Nullable Object, C extends @Nullable Object> IConverter<P, C>
    wrap(IConverter<P, C> converter) {
        return new IConverter<>() {

            @Override
            public void initialize(AbstractProperty p) {
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
     * Write-only converter
     *
     * @param <P>       type of the property side
     * @param <C>       type of the component side
     * @param prop2comp the function to convert value from property side to
     *                  component side
     */
    public static <P extends @Nullable Object, C extends @Nullable Object> IConverter<P, C> listen(final Function<P, C> prop2comp) {
        return converter(prop2comp, o -> {
            throw new WriteOnlyException();
        });
    }

    /**
     * Write-only converter
     *
     * @param <P>       type of the property side
     * @param <C>       type of the component side
     * @param prop2comp the function to convert value from property side to
     *                  component side
     */
    public static <P extends @Nullable Object, C extends @Nullable Object, K>
    IConverterWithContext<P, C, K> listen(final PropertiesContext<K> context,
                                          final BiFunction<P, K, C> prop2comp) {
        return converter(context, prop2comp, (o, k) -> {
            throw new WriteOnlyException();
        });
    }


    public static <P extends @Nullable Object, C extends @Nullable Object>
    IConverter<P, C> converter(final Function<P, C> prop2comp,
                               final FunctionWithException<C, P, ConversionException> comp2prop) {
        return new IConverter<>() {

            @Override
            public C convertPropertyValueToComponentValue(final P propertyValue) {
                return prop2comp.apply(propertyValue);
            }

            @Override
            public P convertComponentValueToPropertyValue(final C componentValue) throws ConversionException {
                return comp2prop.apply(componentValue);
            }

        };
    }

    public static <P extends @Nullable Object, C extends @Nullable Object, K>
    IConverterWithContext<P, C, K> converter(final PropertiesContext<K> context,
                                             final BiFunction<P, K, C> prop2comp,
                                             final BiFunctionWithException<C, K, P, ConversionException> comp2prop) {
        return new IConverterWithContext<>() {

            @Override
            public C convertPropertyValueToComponentValue(final P propertyValue, K context) {
                return prop2comp.apply(propertyValue, context);
            }

            @Override
            public P convertComponentValueToPropertyValue(final C componentValue, K context) throws ConversionException {
                return comp2prop.apply(componentValue, context);
            }

            @Override
            public PropertiesContext<K> contextProperties() {
                return context;
            }

        };
    }


    public static <P, C> IConverter<List<P>, List<C>> listConverter(final Function<P, C> prop2comp,
                  final FunctionWithException<C, P, ConversionException> comp2prop) {
        return new IConverter<>() {

            @Override
            public List<C> convertPropertyValueToComponentValue(final List<P> propertyValue) {
                return propertyValue.stream().map(prop2comp).toList();
            }

            @Override
            public List<P> convertComponentValueToPropertyValue(final List<C> componentValue) throws ConversionException {
                final var result = new ArrayList<P>(componentValue.size());
                for (final var compValue : componentValue) {
                    result.add(comp2prop.apply(compValue));
                }
                return result;
            }

        };
    }

    public static <V> IConverter<List<V>, List<V>> filter(final Predicate<V> filter) {
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

    public static <P, C> IConverter<List<P>, List<C>> listConverter(final Function<P, C> prop2comp) {
        return new IConverter<>() {

            @Override
            public List<C> convertPropertyValueToComponentValue(final List<P> propertyValue) {
                return propertyValue.stream().map(prop2comp).toList();
            }

            @Override
            public List<P> convertComponentValueToPropertyValue(final List<C> componentValue) {
                throw new WriteOnlyException();
            }

        };
    }

    public static <P, C> IConverter<List<P>, List<C>> listConverter(final IConverter<P, C> prop2comp) {
        return new IConverter<>() {

            @Override
            public List<C> convertPropertyValueToComponentValue(final List<P> propertyValue) {
                return propertyValue.stream().map(prop2comp::convertPropertyValueToComponentValue)
                        .toList();
            }

            @Override
            public List<P> convertComponentValueToPropertyValue(final List<C> componentValue) {
                throw new WriteOnlyException();
            }

        };
    }

    public static <C extends @Nullable Object> IConverter<Integer, C> intConverter(final IntFunction<C> prop2comp,
                                                          final FunctionWithException<C, Integer, ConversionException> comp2prop) {
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

    public static <C extends @Nullable Object> IConverter<Long, C> longConverter(final LongFunction<C> prop2comp,
                                                        final FunctionWithException<C, Long, ConversionException> comp2prop) {
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

    public static <C extends @Nullable Object> IConverter<Boolean, C> booleanConverter(final Function<Boolean, C> prop2comp,
                                                              final FunctionWithException<C, Boolean, ConversionException> comp2prop) {
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

    public static <C extends @Nullable Object> IConverter<Float, C> floatConverter(final Function<Float, C> prop2comp,
                                                          final FunctionWithException<C, Float, ConversionException> comp2prop) {
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

    public static IConverter<ConversionError, String> guiErrorToString() {
        return new ConversionErrorToStringConverter("");
    }

    public static IConverter<ConversionError, String> guiErrorToString(String noError) {
        return new ConversionErrorToStringConverter(noError);
    }

    public static <K, V, U> IConverter<Map<K, V>, U> mapContains(K key, U either, U or) {
        return listen(e -> e.containsKey(key) ? either : or);
    }

    public static IConverter<@Nullable String, @Nullable String> toSingleLine(char separator) {
        return converter(s -> s, s -> s != null ? s.replace('\n', separator) : null);
    }

    public static <P> IConverter<P, P> identity() {
        return converter(Function.identity(), LambdaExt.identity());
    }

    public static IConverter<Integer, Integer> intIdentity() {
        return intConverter(i -> i, LambdaExt.identity());
    }

    public static IConverter<Long, Long> longIdentity() {
        return longConverter(l -> l, LambdaExt.identity());
    }

    public static IConverter<Boolean, Boolean> booleanIdentity() {
        return booleanConverter(Function.identity(), LambdaExt.identity());
    }

    public static IConverter<Float, Float> floatIdentity() {
        return floatConverter(Function.identity(), LambdaExt.identity());
    }

    public static <P extends Number> FunctionWithException<String, P, ConversionException> numberToString(
            final FunctionWithException<String, P, NumberFormatException> converter) {
        return c -> {
            try {
                return converter.apply(c);
            } catch (final NumberFormatException e) {
                throw new ConversionException("Cannot convert to number", e);
            }
        };
    }

    public static IConverter<@Nullable String, @Nullable String> stringToString() {
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

    public static <P> IConverter<P, String> objectToString() {
        return listen(Object::toString);
    }

    public static <T extends Number> IConverter<T, String> numberToSize() {
        return listen(FormatterHelper::toSize);
    }

    public static <P extends @Nullable Object> IConverter<P, Boolean> isNotNull() {
        return listen(Objects::nonNull);
    }

    public static <P> IConverter<P, P> identity(final BiConsumer<Boolean, P> consumer) {
        return new IConverter<>() {

            @Override
            public P convertPropertyValueToComponentValue(final P propertyValue) {
                consumer.accept(true, propertyValue);
                return propertyValue;
            }

            @Override
            public P convertComponentValueToPropertyValue(final P componentValue) {
                consumer.accept(false, componentValue);
                return componentValue;
            }
        };
    }

    public static <C extends @Nullable Object> IConverter<Boolean, C> either(final Supplier<C> either, final Supplier<C> or) {
        return new IConverter<>() {

            @Override
            public C convertPropertyValueToComponentValue(final Boolean propertyValue) {
                if (propertyValue) {
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

    public static <P extends Enum<P>> IConverter<P, Boolean> matches(P enumValue) {
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
                if (!componentValue) {
                    throw new ChainInhibitedException("Component is false");
                }
                return enumValue;
            }

        };
    }

}
