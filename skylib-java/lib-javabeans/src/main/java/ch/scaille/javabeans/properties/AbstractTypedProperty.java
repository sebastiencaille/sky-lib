package ch.scaille.javabeans.properties;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import ch.scaille.javabeans.IBindingController;
import ch.scaille.javabeans.IChainBuilder;
import ch.scaille.javabeans.IChainBuilderFactory;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.IVetoer.TransmitMode;
import ch.scaille.javabeans.PropertyEvent.EventKind;
import ch.scaille.javabeans.converters.IConverterWithContext;
import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.javabeans.persisters.Persisters;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A property with a typed value.
 * <p>
 *
 * @param <T> the type of the object contained in the property
 * @author Sebastien Caille
 */
public abstract class AbstractTypedProperty<T> extends AbstractProperty implements IChainBuilder<T> {

    private final List<IConverter<T, T>> implicitConverters = new ArrayList<>();

    @NonNull
    private transient IPersister<T> persister = Persisters.dummy();

    protected abstract T replaceValue(T newValue);

    public abstract T getObjectValue();

    @NonNull
    public abstract IChainBuilderFactory<T> createBindingChain();

    protected AbstractTypedProperty(final String name, final IPropertiesOwner model) {
        super(name, model.getPropertySupport());
        setErrorNotifier(model.getErrorNotifier());
    }

    protected AbstractTypedProperty(final String name, final IPropertiesGroup propertySupport) {
        super(name, propertySupport);
    }

    public void setPersister(@Nullable final IPersister<T> persister) {
        this.persister = Objects.requireNonNullElseGet(persister, Persisters::dummy);
    }

    public void addImplicitConverter(IConverter<T, T> converter) {
        implicitConverters.add(converter);
    }

    @Override
    public void load(final @NonNull Object caller) {
        setObjectValue(caller, persister.get());
    }

    @Override
    public void save() {
        try {
            persister.set(getObjectValue());
        } catch (final RuntimeException e) {
            throw new IllegalStateException("Failed to set property " + getName(), e);
        }
    }

    public void setObjectValueFromComponent(final @NonNull Object caller, final T newValue) {
        if (mustSendToProperty()) {
            setObjectValue(caller, newValue);
        }
    }

    public @NonNull AbstractTypedProperty<T> configureTyped(final Consumer<AbstractTypedProperty<T>>... propertyConfigurer) {
        Stream.of(propertyConfigurer).forEach(prop -> prop.accept(this));
        return this;
    }

    @NonNull
    @Override
    public <C> IChainBuilder<C> bind(final @NonNull IConverter<T, C> binding) {
        return createBindingChainWithConv().bind(binding);
    }

    @NonNull
    @Override
    public <C> IChainBuilder<C> bind(final @NonNull Function<T, C> binding) {
        return createBindingChainWithConv().bind(binding);
    }

    @NonNull
    @Override
    public IBindingController bind(final @NonNull IComponentBinding<T> binding) {
        return createBindingChainWithConv().bind(binding);
    }

    @NonNull
    @Override
    public <C> IChainBuilder<C> bind(@NonNull Function<T, C> prop2Comp,
                                     @NonNull Function<C, T> comp2Prop) {
        return createBindingChainWithConv().bind(prop2Comp, comp2Prop);
    }

    @NonNull
    @Override
    public <C, K> IChainBuilder<C> bind(@NonNull PropertiesContext<K> multiProperties,
                                        @NonNull BiFunction<T, @NonNull K, C> prop2Comp) {
        return createBindingChainWithConv().bind(multiProperties, prop2Comp);
    }

    @NonNull
    @Override
    public <C, K> IChainBuilder<C> bind(@NonNull PropertiesContext<K> multiProperties,
                                        @NonNull BiFunction<T, @NonNull K, C> prop2Comp,
                                        @NonNull BiFunction<C, K, T> comp2Prop) {
        return createBindingChainWithConv().bind(multiProperties, prop2Comp, comp2Prop);
    }

    @NonNull
    @Override
    public <C, K> IChainBuilder<C> bind(@NonNull IConverterWithContext<T, C, @NonNull K> converter) {
        return createBindingChainWithConv().bind(converter);
    }

    /**
     * Executes binding when the property is updated (transmitMode = BOTH only)
     */
    public IBindingController listenActive(final Consumer<T> binding) {
        final var listen = createBindingChain().listen(binding);
        listen.getVetoer().inhibitTransmitToComponentWhen(p -> p.getTransmitMode() != TransmitMode.TRANSMIT);
        return listen;
    }

    /**
     * Executes binding when the property is updated (transmitMode =
     * BOTH|TO_COMPONENT)
     */
    @NonNull
    @Override
    public IBindingController listen(final @NonNull Consumer<T> binding) {
        return createBindingChain().listen(binding);
    }

    private IChainBuilderFactory<T> createBindingChainWithConv() {
        var chain = createBindingChain();
        for (final var conv : implicitConverters) {
            chain = chain.earlyBind(conv);
        }
        return chain;
    }

    protected void setObjectValue(final @NonNull Object caller, @Nullable final T newValue) {
        onValueSet(caller, EventKind.BEFORE);
        try {
            if (!mustSendToComponent()) {
                replaceValue(newValue);
                return;
            }
            final var oldValue = replaceValue(newValue);
            if (oldValue != null || newValue != null) {
                propertySupport.getChangeSupport().firePropertyChange(getName(), caller, oldValue, newValue);
            }
        } finally {
            onValueSet(caller, EventKind.AFTER);
        }
    }

    @Override
    public void flushChanges(@NonNull Object caller) {
        propertySupport.getChangeSupport().firePropertyChange(getName(), this, null, getObjectValue());
    }

    @NonNull
    @Override
    public PropertyChangeEvent getRefreshChangeEvent() {
        return new PropertyChangeEvent(this, getName(), null, getObjectValue());
    }
}
