package ch.scaille.javabeans.properties;

import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.IPropertiesOwner;

/**
 * A property that contains an object.
 *
 * @param <T>
 */
public class ObjectProperty<T extends @Nullable Object> extends ExtendableObjectProperty<T, ObjectProperty<T>> {
    public ObjectProperty(final String name, final IPropertiesOwner model, final T defaultValue) {
        super(name, model, defaultValue);
    }

    public ObjectProperty(final String name, final IPropertiesGroup propertySupport, final T defaultValue) {
        super(name, propertySupport, defaultValue);
    }

    @Override
    public ObjectProperty<T> configureTyped(final Consumer<? super ObjectProperty<T>>... properties) {
        super.configureTyped(properties);
        return this;
    }

}
