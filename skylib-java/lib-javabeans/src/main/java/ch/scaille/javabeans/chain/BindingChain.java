package ch.scaille.javabeans.chain;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.logging.Level;

import ch.scaille.javabeans.IBindingChainDependency;
import ch.scaille.javabeans.IBindingController;
import ch.scaille.javabeans.IChainBuilderFactory;
import ch.scaille.javabeans.IVetoer;
import ch.scaille.javabeans.Logging;
import ch.scaille.javabeans.chain.Vetoer.TransmitMode;
import ch.scaille.javabeans.converters.ChainInhibitedException;
import ch.scaille.javabeans.converters.ConversionErrors;
import ch.scaille.javabeans.converters.ConversionException;
import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.AbstractProperty.ErrorNotifier;

/**
 * Holds and applies the bindings in chains. Each chain listens to the property and to a
 * gui component, and propagates the updates from the property to the component, and from the component to the properties.
 * <p>
 * 
 * </p>
 *
 * @author scaille
 *
 */
public class BindingChain implements IBindingChainModifier {

	/**
	 * To enable / disable the binding
	 */
	private Vetoer vetoer;

	/**
	 * All the links (converters, ...)
	 */
	private final List<Link> links = new ArrayList<>();

	private final AbstractProperty property;

	private final PropertyChangeListener valueUpdateListener;

	private final ErrorNotifier errorNotifier;

	private final List<IBindingChainDependency> dependencies = new ArrayList<>();
	


	public BindingChain(final AbstractProperty prop, final ErrorNotifier errorNotifier) {
		this.property = prop;
		this.errorNotifier = errorNotifier;
		// handle property change
		this.valueUpdateListener = this::propagatePropertyChange;
	}

	private void propagatePropertyChange(final PropertyChangeEvent evt) {
		if (vetoer != null && !vetoer.mustSendToComponent(BindingChain.this)) {
			return;
		}
		var value = evt.getNewValue();
		Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE, () -> "Property change: " + evt.getPropertyName() + ": "
				+ evt.getOldValue() + " -> " + evt.getNewValue());
		Logging.MVC_EVENTS_DEBUGGER.log(Level.FINEST, () -> Arrays.toString(new Exception().getStackTrace()));

		for (final var link : links) {
			try {
				value = link.toComponent(value);
			} catch (final ChainInhibitedException e) {
				Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE, () -> "Property change inhibited: " + e.getMessage());
				return;
			} catch (final ConversionException e) {
				errorNotifier.notifyError(property, ConversionErrors.fromException(getProperty(), e));
				return;
			}
		}
		errorNotifier.clearError(property, property);
	}

	/**
	 * Binds to the property
	 * @param <T> the type of the property
	 * @param propertySetter the property setter that must be called then setting the value coming from the components
	 * @return an end of chain, to dynamically control the chain
	 */
	public <T> IChainBuilderFactory<T> bindProperty(final BiConsumer<Object, T> propertySetter) {
		property.addListener(valueUpdateListener);
		links.add(new Link() {
			@Override
			public Object toProperty(final Object component, final Object value) {
				Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE, () -> "Setting property value: " + value);
				propertySetter.accept(component, (T) value);
				return null;
			}

			@Override
			public Object toComponent(final Object value) {
				return value;
			}

			@Override
			public void unbind() {
				// nothing to do
			}

		});
		return new FirstEndOfChain<>(this);
	}

	@Override
	public void addLink(Link link) {
		links.add(link);
	}
	
	@Override
	public void propagateComponentChange(final Object component, final Object componentValue) {
		final var pos = links.size();
		var value = componentValue;
		for (int i = pos - 1; i >= 0; i--) {
			try {
				value = links.get(i).toProperty(component, value);
			} catch (final ChainInhibitedException e) {
				Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE,
						() -> "Component change inhibited: " + e.getMessage());
				return;
			} catch (final ConversionException e) {
				errorNotifier.notifyError(component, ConversionErrors.fromException(getProperty(), e));
				return;
			}
		}
		errorNotifier.clearError(component, getProperty());
	}
	
	@Override
	public void forceViewUpdate() {
		property.fireArtificialChange(this);
	}

	@Override
	public AbstractProperty getProperty() {
		return property;
	}

	@Override
	public IBindingController addDependency(final IBindingChainDependency dependency) {
		dependency.register(this);
		dependencies.add(dependency);
		return this;
	}

	@Override
	public void unbind() {
		property.removeListener(valueUpdateListener);
		dependencies.forEach(IBindingChainDependency::unbind);
		links.forEach(Link::unbind);
	}

	@Override
	public IVetoer getVetoer() {
		return getVetoerImpl();
	}
	
	public Vetoer getVetoerImpl() {
		if (vetoer == null) {
			vetoer = new Vetoer(TransmitMode.BOTH);
		}
		return vetoer;
	}

	public void setVetoer(final Vetoer vetoer) {
		this.vetoer = vetoer;
	}

	/**
	 * Allows to block the transmission of the value
	 */
	public IBindingController addPropertyInhibitor(Predicate<AbstractProperty> inhibitor) {
		getVetoerImpl().inhibitTransmitToComponentWhen(inhibitor);
		return this;
	}

	@Override
	public boolean mustSendToProperty(IBindingChainModifier chain) {
		return getVetoerImpl().mustSendToProperty(chain);
	}
	
	@Override
	public String toString() {
		return "Chain of " + property.getName();
	}

}
