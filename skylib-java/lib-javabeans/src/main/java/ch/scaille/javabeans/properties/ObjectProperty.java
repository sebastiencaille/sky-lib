package ch.scaille.javabeans.properties;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import ch.scaille.javabeans.IChainBuilderFactory;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.chain.BindingChain;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A property that contains an object.
 * <p>
 *
 * @param <T>
 */
public class ObjectProperty<T> extends AbstractTypedProperty<T> {

    private final T defaultValue;

    private T value;

    public ObjectProperty(@NonNull final String name, final @NonNull IPropertiesOwner model, final T defaultValue) {
        super(name, model);
        this.defaultValue = defaultValue;
        value = defaultValue;
    }

    public ObjectProperty(final @NonNull String name, final @NonNull IPropertiesOwner model) {
        this(name, model, null);
    }

    public ObjectProperty(@NonNull final String name, @NonNull final IPropertiesGroup propertySupport, final T defaultValue) {
        super(name, propertySupport);
        this.defaultValue = defaultValue;
        value = defaultValue;
    }

    public ObjectProperty(final String name, final IPropertiesGroup propertySupport) {
        this(name, propertySupport, null);
    }

    @Override
    public @NonNull IChainBuilderFactory<T> createBindingChain() {
        return new BindingChain(this, errorNotifier).bindProperty(this::setObjectValueFromComponent);
    }

    @Override
    public @NonNull ObjectProperty<T> configureTyped(final Consumer<AbstractTypedProperty<T>>... properties) {
        super.configureTyped(properties);
        return this;
    }

    public <U> ObjectProperty<U> child(final @NonNull String name, final @NonNull Function<T, U> getter, final @NonNull BiConsumer<T, U> setter) {
        final var child = new ObjectProperty<U>(getName() + "-" + name, propertySupport);
        asChild(child, getter, setter);
        return child;
    }

    public <U> void asChild(final @NonNull ObjectProperty<U> child, final @NonNull Function<T, U> getter,
                            final BiConsumer<T, U> setter) {
        this.addListener(p -> child.setValue(this, getter.apply(this.getValue())));
        child.addListener(c -> {
            final var oldValue = getter.apply(this.getValue());
            final var newValue = child.getValue();
            if (!Objects.equals(oldValue, newValue)) {
                setter.accept(getValue(), newValue);
                this.forceChanged(c.getSource());
            }
        });
    }

    @Override
    protected T replaceValue(final T newValue) {
        final var oldValue = value;
        value = newValue;
        return oldValue;
    }

    public void setValue(final Object caller, @Nullable final T newValue) {
        setObjectValue(caller, newValue);
    }

    @Override
    public T getObjectValue() {
        return getValue();
    }

    public void forceChanged(final Object caller) {
        propertySupport.getChangeSupport().firePropertyChange(getName(), caller, null, getValue());
    }

    public T getValue() {
        return value;
    }

    public Optional<T> optional() {
        return Optional.ofNullable(value);
    }

    @Override
    public void reset(final @NonNull Object caller) {
        setObjectValue(this, defaultValue);
    }

    public boolean isSet() {
        return getObjectValue() != null;
    }

    public <R> R map(@NonNull Function<T, R> mapper) {
        return mapper.apply(value);
    }

}
