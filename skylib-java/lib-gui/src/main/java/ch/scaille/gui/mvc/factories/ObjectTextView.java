package ch.scaille.gui.mvc.factories;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import ch.scaille.javabeans.Converters;
import ch.scaille.javabeans.converters.ConversionException;
import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.util.helpers.LambdaExt.FunctionWithException;

/**
 * To display some text based on the content of an object
 */
public abstract class ObjectTextView<T> {

	private final T object;

	protected ObjectTextView(final T object) {
		this.object = object;
	}

	public T getObject() {
		return object;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj != null && obj.getClass().equals(this.getClass())
				&& Objects.equals(((ObjectTextView<?>) obj).object, object);
	}

	@Override
	public int hashCode() {
		return object.hashCode();
	}

	/**
	 * To display some text based on the content of an object using a function
	 */
	public static class FunctionObjectTextView<T> {

		private final Function<T, String> objToText;

		public FunctionObjectTextView(final Function<T, String> objToText) {
			this.objToText = objToText;
		}

		public ObjectTextView<T> apply(T object) {
			return new ObjectTextView<>(object) {
				@Override
				public String toString() {
					if (object == null) {
						return "";
					}
					return objToText.apply(object);
				}
			};
		}
	}
	
	/**
	 * To display some text based on the content of an object using a function
	 */
	public static class BiFunctionObjectTextView<T, U> {

		private final BiFunction<T, U, String> objToText;

		public BiFunctionObjectTextView(final BiFunction<T, U, String> objToText) {
			this.objToText = objToText;
		}

		public ObjectTextView<T> apply(T object, U context) {
			return new ObjectTextView<>(object) {
				@Override
				public String toString() {
					if (object == null) {
						return "";
					}
					return objToText.apply(object, context);
				}
			};
		}
	}

	public static <T> FunctionObjectTextView<T> object2Text(final Function<T, String> objToText) {
		return new FunctionObjectTextView<>(objToText);
	}

	public static <T, U> BiFunctionObjectTextView<T, U> biObject2Text(final BiFunction<T, U, String> objToText) {
		return new BiFunctionObjectTextView<>(objToText);
	}
	
	public static <T> IConverter<T, ObjectTextView<T>> converter(final Function<T, String> objToText) {
		return Converters.converter(prop2Comp(objToText), comp2prop());
	}

	public static <T> Function<T, ObjectTextView<T>> prop2Comp(final Function<T, String> objToText) {
		return o -> object2Text(objToText).apply(o);
	}

	public static <T> FunctionWithException<ObjectTextView<T>, T, ConversionException> comp2prop() {
		return tv -> tv != null ? tv.getObject() : null;
	}
}