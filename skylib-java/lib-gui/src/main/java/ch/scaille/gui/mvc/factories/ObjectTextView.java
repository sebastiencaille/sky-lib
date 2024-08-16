package ch.scaille.gui.mvc.factories;

import java.util.Objects;
import java.util.function.Function;

import ch.scaille.javabeans.Converters;
import ch.scaille.javabeans.converters.IConverter;

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
		return obj != null && obj.getClass().equals(this.getClass()) && Objects.equals(((ObjectTextView<?>) obj).object, object);
	}

	@Override
	public int hashCode() {
		return object.hashCode();
	}

	public static class FunctionObjectTextView<T> extends ObjectTextView<T> {
		private final Function<T, String> objToText;

		public FunctionObjectTextView(final T object, final Function<T, String> objToText) {
			super(object);
			this.objToText = objToText;
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public String toString() {
			if (getObject() == null) {
				return "";
			}
			return objToText.apply(getObject());
		}

	}

	public static <T> IConverter<T, ObjectTextView<T>> converter(final Function<T, String> objToText) {
		return Converters.converter(o -> new FunctionObjectTextView<>(o, objToText),
				tv -> tv != null ? tv.getObject() : null);
	}
}