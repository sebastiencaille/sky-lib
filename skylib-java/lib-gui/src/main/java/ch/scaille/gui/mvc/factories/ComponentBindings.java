package ch.scaille.gui.mvc.factories;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class ComponentBindings {

    private static final String BINDING_TO_WRITE_ONLY_COMPONENT = "Binding to write only component";

    private ComponentBindings() {
    }

    public interface ValueSetter<C, P extends @Nullable Object> {
        void setComponentValue(C component, IComponentChangeSource property, P value);
    }

    /**
     * @param <C> component type
     * @param <P> property-side type
     * @param <L> listener type
     */
    public static <C, P extends @Nullable Object, L> IComponentBinding<P>
    component(final C component,
              final BiFunction<C, IComponentLink<P>, L> addValueChangeListener,
              final BiConsumer<C, L> removeValueChangeListener, final ValueSetter<C, P> setComponentValue) {
        return new IComponentBinding<>() {

            @Nullable
            private L listener;

            @Override
            public void addComponentValueChangeListener(final IComponentLink<P> link) {
                listener = addValueChangeListener.apply(component, link);
            }

            @Override
            public void setComponentValue(final IComponentChangeSource source, final P value) {
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
    public static <P extends @Nullable Object> IComponentBinding<P>
    listen(final BiConsumer<IComponentChangeSource, P> setComponentValue) {
        return new IComponentBinding<>() {

            @Override
            public void addComponentValueChangeListener(final IComponentLink<P> link) {
                // component value never read
            }

            @Override
            public void removeComponentValueChangeListener() {
                // component value never read
            }

            @Override
            public void setComponentValue(final IComponentChangeSource source, final P value) {
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
    public static <P extends @Nullable Object> IComponentBinding<P>
    listen(final Consumer<P> setComponentValue) {
        return new IComponentBinding<>() {

            @Override
            public void addComponentValueChangeListener(final IComponentLink<P> link) {
                // component value never read
            }

            @Override
            public void removeComponentValueChangeListener() {
                // component value never read
            }

            @Override
            public void setComponentValue(final IComponentChangeSource source, @Nullable final P value) {
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
    public static <C, P extends @Nullable Object> IComponentBinding<P>
    listen(final C component, final ValueSetter<C, P> setComponentValue) {
        return new IComponentBinding<>() {

            @Override
            public void addComponentValueChangeListener(final IComponentLink<P> link) {
                // component value never read
            }

            @Override
            public void removeComponentValueChangeListener() {
                // component value never read
            }

            @Override
            public void setComponentValue(final IComponentChangeSource source, final P value) {
                setComponentValue.setComponentValue(component, source, value);
            }

            @Override
            public String toString() {
                return BINDING_TO_WRITE_ONLY_COMPONENT;
            }
        };
    }
}
