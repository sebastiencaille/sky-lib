package ch.scaille.gui.mvc.factories;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jspecify.annotations.Nullable;

import ch.scaille.javabeans.converters.ConversionException;
import ch.scaille.javabeans.converters.Converters;
import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.util.helpers.LambdaExt.FunctionWithException;
import lombok.Getter;

/**
 * To display some text based on the content of an object
 */
@Getter
public class ObjectTextView<T extends @Nullable Object> {

	private final T object;
    private final Function<T, String> textSupplier;

    protected ObjectTextView(final T object, Function<T, String> textSupplier) {
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
		return textSupplier.apply(object);
	}

	/**
	 * To convert an object to a text
	 * @param objToText the conversion function
	 */
	public static <T> IConverter<@Nullable T, @Nullable ObjectTextView<T>>
	converter(final Function<T, String> objToText) {
		return Converters.converter(obj2Text(objToText), text2Obj());
	}

	/**
	 * To convert an object to a text
	 * @param objToText the conversion function
	 */
	public static <T> IConverter<T, ObjectTextView<T>> convertWo(final Function<T, String> objToText) {
		return Converters.converter(o -> new ObjectTextView<>(o, objToText), ObjectTextView::getObject);
	}

	public static <T extends @Nullable Object> Function<T, @Nullable ObjectTextView<T>> obj2Text(final Function<T, String> objToText) {
		return o -> o != null ? new ObjectTextView<>(o, objToText) : null;
	}

	public static <T extends @Nullable Object, K extends @Nullable Object> BiFunction<T, K, ObjectTextView<T>> contextualObject2Text(final BiFunction<T, K, String> objToText) {
		return (o, k) -> new ObjectTextView<>(o, obj -> objToText.apply(obj, k));
	}

	public static <T extends @Nullable Object> FunctionWithException<@Nullable ObjectTextView<T>, T, ConversionException> text2Obj() {
		return otv -> otv != null ? otv.getObject(): null;

	}
}