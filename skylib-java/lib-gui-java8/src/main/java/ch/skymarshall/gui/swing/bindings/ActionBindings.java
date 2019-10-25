package ch.skymarshall.gui.swing.bindings;

import java.util.function.Consumer;

import ch.skymarshall.gui.mvc.IComponentBinding;
import ch.skymarshall.gui.mvc.IComponentLink;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;

public class ActionBindings {

	public static <T> IComponentBinding<T> applier(final Consumer<T> action) {
		return new IComponentBinding<T>() {

			@Override
			public void addComponentValueChangeListener(final IComponentLink<T> link) {
// 				no op
			}

			@Override
			public void setComponentValue(final AbstractProperty source, final T value) {
				action.accept(value);
			}

			@Override
			public void removeComponentValueChangeListener() {
				// no op
			}

		};
	}

}
