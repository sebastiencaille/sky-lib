package ch.scaille.gui.mvc.factories;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class ComponentBindings {

	private static final String BINDING_TO_WRITE_ONLY_COMPONENT = "Binding to write only component";

	private ComponentBindings() {
	}

	public interface ValueSetter<C, T> {
		void setComponentValue(@NonNull C component, IComponentChangeSource property, T value);
	}

	public static <C, T, L> IComponentBinding<T> component(final @NonNull C component,
			final BiFunction<C, IComponentLink<T>, L> addValueChangeListener,
			final BiConsumer<C, L> removeValueChangeListener, final ValueSetter<C, T> setComponentValue) {
		return new IComponentBinding<>() {

			@Nullable
			private L listener;

			@Override
			public void addComponentValueChangeListener(final @NonNull IComponentLink<T> link) {
				listener = addValueChangeListener.apply(component, link);
			}

			@Override
			public void setComponentValue(final @NonNull IComponentChangeSource source, final T value) {
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
     */
	public static <T> IComponentBinding<T> listen(final BiConsumer<IComponentChangeSource, T> setComponentValue) {
		return new IComponentBinding<>() {

			@Override
			public void addComponentValueChangeListener(final @NonNull IComponentLink<T> link) {
				// component value never read
			}

			@Override
			public void removeComponentValueChangeListener() {
				// component value never read
			}

			@Override
			public void setComponentValue(final @NonNull IComponentChangeSource source, final T value) {
				setComponentValue.accept(source, value);
			}

			@Override
			public String toString() {
				return BINDING_TO_WRITE_ONLY_COMPONENT;
			}

		};
	}

	/**
	 *
	 * @param setComponentValue (source, value)
     */
	public static <T> IComponentBinding<T> listen(final Consumer<T> setComponentValue) {
		return new IComponentBinding<>() {

			@Override
			public void addComponentValueChangeListener(final @NonNull IComponentLink<T> link) {
				// component value never read
			}

			@Override
			public void removeComponentValueChangeListener() {
				// component value never read
			}

			@Override
			public void setComponentValue(final @NonNull IComponentChangeSource source, final T value) {
				setComponentValue.accept(value);
			}

			@Override
			public String toString() {
				return BINDING_TO_WRITE_ONLY_COMPONENT;
			}

		};
	}

	/**
	 *
	 * @param setComponentValue (source, value)
     */
	public static <C, T> IComponentBinding<T> listen(final @NonNull C component, final ValueSetter<C, T> setComponentValue) {
		return new IComponentBinding<>() {

			@Override
			public void addComponentValueChangeListener(final @NonNull IComponentLink<T> link) {
				// component value never read
			}

			@Override
			public void removeComponentValueChangeListener() {
				// component value never read
			}

			@Override
			public void setComponentValue(final @NonNull IComponentChangeSource source, final T value) {
				setComponentValue.setComponentValue(component, source, value);
			}

			@Override
			public String toString() {
				return BINDING_TO_WRITE_ONLY_COMPONENT;
			}
		};
	}
}
