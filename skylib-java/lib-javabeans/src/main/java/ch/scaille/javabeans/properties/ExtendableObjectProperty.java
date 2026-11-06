package ch.scaille.javabeans.properties;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import ch.scaille.javabeans.IChainBuilderFactory;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.chain.BindingChain;
import lombok.Getter;

/**
 * A property that contains an object.
 *
 * @param <T>
 */
public class ExtendableObjectProperty<T extends @Nullable Object, S extends ExtendableObjectProperty<T, S>>
        extends AbstractTypedProperty<T, S> {

    private static final long serialVersionUID = 1L;

    private final T defaultValue;

    @Getter
    private T value;

    public ExtendableObjectProperty(final String name, final IPropertiesOwner model, final T defaultValue) {
        super(name, model);
        this.defaultValue = defaultValue;
        value = defaultValue;
    }

    public ExtendableObjectProperty(final String name, final IPropertiesGroup propertySupport, final T defaultValue) {
        super(name, propertySupport);
        this.defaultValue = defaultValue;
        value = defaultValue;
    }

    @Override
    public IChainBuilderFactory<T> createBindingChain() {
        return new BindingChain(this, errorNotifier).bindProperty(this::setObjectValueFromComponent);
    }

    public <U extends @Nullable Object> ObjectProperty<U> child(final String name, final Function<T, U> getter, final BiConsumer<T, U> setter) {
        final var child = new ObjectProperty<>(getName() + "-" + name, propertySupport, getter.apply(getValue()));
        asChild(child, getter, setter);
        return child;
    }

    public <U extends @Nullable Object> void asChild(final ObjectProperty<U> child, final Function<T, U> getter,
                                                     final BiConsumer<T, U> setter) {
        this.addListener(_ -> child.setValue(this, getter.apply(this.getValue())));
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

    public void setValue(final Object caller, final T newValue) {
        setObjectValue(caller, newValue);
    }

    @Override
    public T getObjectValue() {
        return getValue();
    }

    public void forceChanged(final Object caller) {
        propertySupport.getChangeSupport().firePropertyChange(getName(), caller, null, getValue());
    }

    public Optional<@NonNull T> optional() {
        return Optional.ofNullable(value);
    }

    @Override
    public void reset(final Object caller) {
        setObjectValue(this, defaultValue);
    }

    public boolean isSet() {
        return getObjectValue() != null;
    }

    public <R> R map(Function<T, R> mapper) {
        return mapper.apply(value);
    }

}
