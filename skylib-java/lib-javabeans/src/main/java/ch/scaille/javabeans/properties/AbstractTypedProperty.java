package ch.scaille.javabeans.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import ch.scaille.javabeans.ContextProperties;
import ch.scaille.javabeans.IBindingController;
import ch.scaille.javabeans.IChainBuilder;
import ch.scaille.javabeans.IChainBuilderFactory;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.IVetoer.TransmitMode;
import ch.scaille.javabeans.PropertyEvent.EventKind;
import ch.scaille.javabeans.converters.IContextualConverter;
import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.javabeans.persisters.Persisters;

/**
 * A property with a typed value.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> the type of the object contained in the property
 */
public abstract class AbstractTypedProperty<T> extends AbstractProperty implements IChainBuilder<T> {

	private final List<IConverter<T, T>> implicitConverters = new ArrayList<>();

	private transient IPersister<T> persister = Persisters.dummy();

	protected abstract T replaceValue(T newValue);

	public abstract T getObjectValue();

	public abstract IChainBuilderFactory<T> createBindingChain();

	protected AbstractTypedProperty(final String name, final IPropertiesOwner model) {
		super(name, model.getPropertySupport());
		setErrorNotifier(model.getErrorNotifier());
	}

	protected AbstractTypedProperty(final String name, final IPropertiesGroup propertySupport) {
		super(name, propertySupport);
	}

	public void setPersister(final IPersister<T> persister) {
		if (persister == null) {
			this.persister = Persisters.dummy();
		} else {
			this.persister = persister;
		}
	}

	public void addImplicitConverter(IConverter<T, T> converter) {
		implicitConverters.add(converter);
	}

	@Override
	public void load(final Object caller) {
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

	public void setObjectValueFromComponent(final Object caller, final T newValue) {
		if (mustSendToProperty()) {
			setObjectValue(caller, newValue);
		}
	}

	public AbstractTypedProperty<T> configureTyped(final Consumer<AbstractTypedProperty<T>>... propertyConfigurer) {
		Stream.of(propertyConfigurer).forEach(prop -> prop.accept(this));
		return this;
	}


	@Override
	public <C> IChainBuilder<C> bind(final IConverter<T, C> binding) {
		return createBindingChainWithConv().bind(binding);
	}

	@Override
	public <C> IChainBuilder<C> bind(final Function<T, C> binding) {
		return createBindingChainWithConv().bind(binding);
	}

	@Override
	public IBindingController bind(final IComponentBinding<T> binding) {
		return createBindingChainWithConv().bind(binding);
	}
	
	@Override
	public <C> IChainBuilder<C> bind(Function<T, C> prop2Comp, Function<C, T> comp2Prop) {
		return createBindingChainWithConv().bind(prop2Comp, comp2Prop);
	}
	
	
	@Override
	public <C, K> IChainBuilder<C> bind(ContextProperties<K> multiProperties, BiFunction<T, K, C> prop2Comp) {
		return createBindingChainWithConv().bind(multiProperties, prop2Comp);
	}
	
	@Override
	public <C, K> IChainBuilder<C> bind(ContextProperties<K> multiProperties, BiFunction<T, K, C> prop2Comp,
			BiFunction<C, K, T> comp2Prop) {
		return createBindingChainWithConv().bind(multiProperties, prop2Comp, comp2Prop);
	}
	
	@Override
	public <C, K> IChainBuilder<C> bind(IContextualConverter<T, C, K> converter) {
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
	@Override
	public IBindingController listen(final Consumer<T> binding) {
		return createBindingChain().listen(binding);
	}

	private IChainBuilderFactory<T> createBindingChainWithConv() {
		var chain = createBindingChain();
		for (final var conv : implicitConverters) {
			chain = chain.earlyBind(conv);
		}
		return chain;
	}

	protected void setObjectValue(final Object caller, final T newValue) {
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
	public void fireArtificialChange(final Object caller) {
		propertySupport.getChangeSupport().firePropertyChange(getName(), caller, null, getObjectValue());
	}

}
