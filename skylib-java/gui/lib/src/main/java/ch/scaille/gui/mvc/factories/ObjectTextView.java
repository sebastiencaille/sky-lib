package ch.scaille.gui.mvc.factories;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import ch.scaille.javabeans.converters.ConversionException;
import ch.scaille.javabeans.converters.Converters;
import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.util.helpers.LambdaExt.FunctionWithException;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

/**
 * To display some text based on the content of an object
 */
@Getter
public class ObjectTextView<T extends @Nullable Object> {

	private final T object;
    private final Supplier<String> textSupplier;

    protected ObjectTextView(final T object, Supplier<String> textSupplier) {
		this.object = object;
        this.textSupplier = textSupplier;
    }

	@Override
	public boolean equals(@Nullable final Object obj) {
		return obj != null && obj.getClass().equals(this.getClass())
				&& Objects.equals(((ObjectTextView<?>) obj).object, object);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(object);
	}

	@Override
	public String toString() {
		return textSupplier.get();
	}

	/**
	 * To convert an object to a text
	 * @param objToText the conversion function
	 */
	public static <T extends @Nullable Object> IConverter<T, @Nullable ObjectTextView<T>> converter(final Function<T, String> objToText) {
		return Converters.converter(obj2Text(objToText), text2Obj());
	}

	public static <T extends @Nullable Object> Function<T, @Nullable ObjectTextView<T>> obj2Text(final Function<T, String> objToText) {
		return o -> new ObjectTextView<>(o, () -> objToText.apply(o));
	}

	public static <T extends @Nullable Object, K extends @Nullable Object> BiFunction<T, K, @Nullable ObjectTextView<T>> contextualObject2Text(final BiFunction<T, K, String> objToText) {
		return (o, k) -> new ObjectTextView<>(o, () -> objToText.apply(o, k));
	}

	public static <T extends @Nullable Object> FunctionWithException<@Nullable ObjectTextView<T>, T, ConversionException> text2Obj() {
		return otv ->  otv != null ? otv.getObject() : null;

	}
}