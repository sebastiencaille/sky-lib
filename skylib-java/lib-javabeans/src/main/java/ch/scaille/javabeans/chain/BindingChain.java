package ch.scaille.javabeans.chain;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.logging.Level;

import ch.scaille.javabeans.IBindingChainDependency;
import ch.scaille.javabeans.IBindingController;
import ch.scaille.javabeans.IChainBuilderFactory;
import ch.scaille.javabeans.IVetoer;
import ch.scaille.javabeans.IVetoer.TransmitMode;
import ch.scaille.javabeans.Logging;
import ch.scaille.javabeans.converters.ChainInhibitedException;
import ch.scaille.javabeans.converters.ConversionErrors;
import ch.scaille.javabeans.converters.ConversionException;
import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.AbstractProperty.ErrorNotifier;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

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
@NullMarked
public class BindingChain implements IBindingChainModifier {

	/**
	 * To enable / disable the binding
	 */
	@Setter
    @Nullable
	private Vetoer vetoer;

	/**
	 * All the links (converters, ...)
	 */
	private final List<Link<?, ?>> links = new ArrayList<>();

	private final AbstractProperty property;

	private final PropertyChangeListener valueUpdateListener;

	private final ErrorNotifier errorNotifier;

	private final List<IBindingChainDependency> dependencies = new ArrayList<>();

	public BindingChain(final AbstractProperty property, final ErrorNotifier errorNotifier) {
		this.property = property;
		this.errorNotifier = errorNotifier;
		// handle property change
		this.valueUpdateListener = this::propagatePropertyChange;
	}

	private void propagatePropertyChange(final PropertyChangeEvent evt) {
		if (vetoer != null && !vetoer.mustSendToComponent(BindingChain.this)) {
			Logging.MVC_EVENTS_DEBUGGER.log(Level.FINEST, () -> "Vetoed: " + evt.getPropertyName());
			return;
		}
		var value = evt.getNewValue();
		Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE, () -> "Property change: " + evt.getPropertyName() + ": "
				+ evt.getOldValue() + " -> " + evt.getNewValue());
		Logging.MVC_EVENTS_DEBUGGER.log(Level.FINEST, () -> Arrays.toString(new Exception().getStackTrace()));

		for (final var link : links) {
			try {
				value = ((Link<Object, Object>) link).toComponent(value);
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
	 * @return the end of the chain, to dynamically control the chain
	 */
	public <T> IChainBuilderFactory<T> bindProperty(final BiConsumer<Object, T> propertySetter) {
		property.addListener(valueUpdateListener);
		return linkComponentToProperty(propertySetter);
	}

	public <T> IChainBuilderFactory<T> linkComponentToProperty(final BiConsumer<Object, @Nullable T> propertySetter) {
		links.add(new Link<T, T>() {
			@Override
			@Nullable
			public T toProperty(final Object component, @Nullable final T value) {
				Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE, () -> "Setting property value: " + value);
				propertySetter.accept(component, value);
				return value;
			}

			@Override
			@Nullable
			public T toComponent(@Nullable final T value) {
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
	public <P, C> void addLink(Link<P, C> link) {
		links.add(link);
	}
	
	@Override
	public void propagateComponentChange(final Object component, @Nullable final Object componentValue) {
		final var pos = links.size();
		var value = componentValue;
		for (int i = pos - 1; i >= 0; i--) {
			try {
				value = ((Link<Object, Object>)links.get(i)).toProperty(component, value);
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
	public void flushChanges() {
		propagatePropertyChange(property.getRefreshChangeEvent());
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
	public void stopTransmit() {
		property.setTransmitMode(TransmitMode.STOPPED);
	}
	
	@Override
	public void transmitChangesBothWays() {
		property.setTransmitMode(TransmitMode.TRANSMIT);	
	}

	@Override
	public void transmitChangesOnlyToComponent() {
		property.setTransmitMode(TransmitMode.TO_COMPONENT_ONLY);		
	}
	
	@Override
	public void pauseBinding() {
		getVetoer().pause();
	}
	
	@Override
	public void resumeBinding() {
		final var resumed = getVetoer().resume();
		if (resumed) {
			flushChanges();
		}
	}
		
	@Override
	public void disposeBindings() {
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
			vetoer = new Vetoer(TransmitMode.TRANSMIT);
		}
		return vetoer;
	}

	@Override
	public boolean mustSendToProperty(IBindingChainModifier chain) {
		return getVetoerImpl().mustSendToProperty(chain);
	}

	@NullUnmarked
	private class WeakLink<C> implements Link<C, C> {

		private final WeakReference<Link<C, C>> weakRef;

        private WeakLink(WeakLinkHolder weakRefHolder, Link<C, C> link) {
			weakRef = new WeakReference<>(link);
			// Keep a reference until the controller or whatever is garbage collected
			weakRefHolder.getLinksHolder().add(link);
        }

        @Override
		public C toComponent(C value) throws ConversionException {
			final var link = weakRef.get();
			if (link != null) {
				return link.toComponent(value);
			}
			disposeBindings();
			return null;
		}

		@Override
		public C toProperty(@Nullable Object source, C value) {
			return value;
		}

		@Override
		public void unbind() {
			// noop
		}
	}

	public static WeakLinkHolder weakHolder() {
		return new WeakLinkHolder();
	}

	@Override
	public IBindingController makeWeak(WeakLinkHolder weakLinkHolder) {
        links.replaceAll(link -> new WeakLink<>(weakLinkHolder, (Link<Object, Object>) link));
		return this;
	}

	@Override
	public String toString() {
		return "Chain of " + property.getName();
	}

}
