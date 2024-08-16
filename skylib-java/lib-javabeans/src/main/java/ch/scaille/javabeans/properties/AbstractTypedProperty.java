package ch.scaille.javabeans.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import ch.scaille.javabeans.BindingChain.EndOfChain;
import ch.scaille.javabeans.IPropertiesOwner;
import ch.scaille.javabeans.IBindingController;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.PropertyEvent.EventKind;
import ch.scaille.javabeans.Veto.TransmitMode;
import ch.scaille.javabeans.converters.IConverter;

/**
 * A property with a typed value.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> the type of the object contained in the property
 */
public abstract class AbstractTypedProperty<T> extends AbstractProperty {

	private final List<IConverter<T, T>> implicitConverters = new ArrayList<>();

	private transient IPersister<T> persister;

	protected AbstractTypedProperty(final String name, final IPropertiesOwner model) {
		super(name, model.getPropertySupport());
		setErrorNotifier(model.getErrorNotifier());
	}

	protected AbstractTypedProperty(final String name, final IPropertiesGroup propertySupport) {
		super(name, propertySupport);
	}

	public void setPersister(final IPersister<T> persister) {
		this.persister = persister;
	}

	public void addImplicitConverter(IConverter<T, T> converter) {
		implicitConverters.add(converter);
	}

	@Override
	public void load(final Object caller) {
		if (persister != null) {
			setObjectValue(caller, persister.get());
		}
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

	public <C> EndOfChain<C> bind(final IConverter<T, C> binding) {
		return createBindingChainWithConv().bind(binding);
	}

	public <C> EndOfChain<C> bind(final Function<T, C> binding) {
		return createBindingChainWithConv().bind(binding);
	}

	public IBindingController bind(final IComponentBinding<T> binding) {
		return createBindingChainWithConv().bind(binding);
	}

	private EndOfChain<T> createBindingChainWithConv() {
		var chain = createBindingChain();
		for (final var conv : implicitConverters) {
			chain = chain.bind(conv);
		}
		return chain;
	}

	/**
	 * Executes binding when the property is updated (transmitMode = BOTH only)
	 */
	public IBindingController listenActive(final Consumer<T> binding) {
		final var listen = createBindingChain().listen(binding);
		listen.getVeto().inhibitTransmitToComponentWhen(b -> b.getProperty().getTransmitMode() != TransmitMode.BOTH);
		return listen;
	}

	/**
	 * Executes binding when the property is updated (transmitMode =
	 * BOTH|TO_COMPONENT)
	 */
	public IBindingController listen(final Consumer<T> binding) {
		return createBindingChain().listen(binding);
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

	protected abstract T replaceValue(T newValue);

	public abstract T getObjectValue();

	public abstract EndOfChain<T> createBindingChain();

}
