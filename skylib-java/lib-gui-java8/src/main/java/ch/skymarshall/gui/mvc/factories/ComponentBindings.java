package ch.skymarshall.gui.mvc.factories;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import ch.skymarshall.gui.mvc.IComponentBinding;
import ch.skymarshall.gui.mvc.IComponentLink;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;

public final class ComponentBindings {

	private ComponentBindings() {
	}

	public static interface ValueSetter<C, T> {
		void setComponentValue(C component, AbstractProperty property, T value);
	}

	public static <C, T, L> IComponentBinding<T> component(final C component,
			final BiFunction<C, IComponentLink<T>, L> addValueChangeListener,
			final BiConsumer<C, L> removeValueChangeListener, final ValueSetter<C, T> setComponentValue) {
		return new IComponentBinding<T>() {

			private L listener;

			@Override
			public void addComponentValueChangeListener(final IComponentLink<T> link) {
				listener = addValueChangeListener.apply(component, link);
			}

			@Override
			public void setComponentValue(final AbstractProperty source, final T value) {
				setComponentValue.setComponentValue(component, source, value);
			}

			@Override
			public void removeComponentValueChangeListener() {
				if (listener != null) {
					removeValueChangeListener.accept(component, listener);
				}
			}

			@Override
			public String toString() {
				return "Binding to component " + component;
			}

		};
	}

	/**
	 *
	 * @param setComponentValue (source, value)
	 * @return
	 */
	public static <T> IComponentBinding<T> wo(final BiConsumer<AbstractProperty, T> setComponentValue) {
		return new IComponentBinding<T>() {

			@Override
			public void addComponentValueChangeListener(final IComponentLink<T> link) {
				// component value never read
			}

			@Override
			public void removeComponentValueChangeListener() {
				// component value never read
			}

			@Override
			public void setComponentValue(final AbstractProperty source, final T value) {
				setComponentValue.accept(source, value);
			}

			@Override
			public String toString() {
				return "Binding to write only component";
			}

		};
	}

	/**
	 *
	 * @param setComponentValue (source, value)
	 * @return
	 */
	public static <T> IComponentBinding<T> wo(final Consumer<T> setComponentValue) {
		return new IComponentBinding<T>() {

			@Override
			public void addComponentValueChangeListener(final IComponentLink<T> link) {
				// component value never read
			}

			@Override
			public void removeComponentValueChangeListener() {
				// component value never read
			}

			@Override
			public void setComponentValue(final AbstractProperty source, final T value) {
				setComponentValue.accept(value);
			}

			@Override
			public String toString() {
				return "Binding to write only component";
			}

		};
	}

	/**
	 *
	 * @param setComponentValue (source, value)
	 * @return
	 */
	public static <C, T> IComponentBinding<T> wo(final C component, final ValueSetter<C, T> setComponentValue) {
		return new IComponentBinding<T>() {

			@Override
			public void addComponentValueChangeListener(final IComponentLink<T> link) {
				// component value never read
			}

			@Override
			public void removeComponentValueChangeListener() {
				// component value never read
			}

			@Override
			public void setComponentValue(final AbstractProperty source, final T value) {
				setComponentValue.setComponentValue(component, source, value);
			}

			@Override
			public String toString() {
				return "Binding to write only component";
			}
		};
	}
}
