package ch.skymarshall.gui.swing.bindings;

import java.util.function.Function;

import ch.skymarshall.gui.mvc.converters.Converters;
import ch.skymarshall.gui.mvc.converters.IConverter;

public abstract class ObjectTextView<T> {

	private final T object;

	public ObjectTextView(final T object) {
		this.object = object;
	}

	public T getObject() {
		return object;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj.equals(object);
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
			return objToText.apply(getObject());
		}

	}

	public static <T> IConverter<T, ObjectTextView<T>> converter(final Function<T, String> objToText) {
		return Converters.converter(o -> new FunctionObjectTextView<>(o, objToText), ObjectTextView::getObject);
	}
}