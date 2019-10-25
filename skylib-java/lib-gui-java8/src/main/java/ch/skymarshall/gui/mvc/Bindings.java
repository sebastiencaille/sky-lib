package ch.skymarshall.gui.mvc;

import java.util.function.Consumer;

import ch.skymarshall.gui.mvc.properties.AbstractProperty;
import ch.skymarshall.gui.swing.bindings.DefaultComponentBinding;

public interface Bindings {

	public static <T> IComponentBinding<T> set(final Consumer<T> consumer) {
		return new DefaultComponentBinding<T>() {
			@Override
			public void setComponentValue(final AbstractProperty source, final T value) {
				consumer.accept(value);
			}
		};
	}

}
