package ch.scaille.gui.mvc.factories;

import java.util.Objects;
import java.util.function.Function;

import ch.scaille.javabeans.Converters;
import ch.scaille.javabeans.converters.IConverter;

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
		return Objects.equals(obj, object);
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