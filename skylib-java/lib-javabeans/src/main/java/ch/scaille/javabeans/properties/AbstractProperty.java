package ch.scaille.javabeans.properties;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.swing.event.EventListenerList;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.PropertyEvent;
import ch.scaille.javabeans.PropertyEvent.EventKind;
import ch.scaille.javabeans.Veto.TransmitMode;

/**
 * Provides default type-independant mechanisms of properties.
 * <p>
 *
 * @author Sebastien Caille
 *
 */
public abstract class AbstractProperty implements Serializable {

	public interface ErrorNotifier {
		void notifyError(Object source, ConversionError error);

		void clearError(Object source, AbstractProperty property);

	}

	public static ErrorNotifier emptyErrorNotifier() {
		return new ErrorNotifier() {

			@Override
			public void notifyError(final Object source, final ConversionError error) {
				// noop
			}

			@Override
			public void clearError(final Object source, AbstractProperty property) {
				// noop
			}

		};
	}

	/**
	 * Name of the property
	 */
	private final String name;

	/**
	 * Support to trigger property change
	 */
	protected final transient IPropertiesGroup propertySupport;

	/**
	 * Property related events (before firing, after firing, ...)
	 */
	protected final transient EventListenerList eventListeners = new EventListenerList();

	/**
	 * Error property
	 */
	protected transient ErrorNotifier errorNotifier = emptyErrorNotifier();

	protected transient TransmitMode transmitMode = TransmitMode.NONE;

	public abstract void reset(Object caller);

	public abstract void load(Object caller);

	public abstract void save();

	public abstract void fireArtificialChange(Object caller);

	protected AbstractProperty(final String name, final IPropertiesGroup propertySupport) {
		this.name = name;
		this.propertySupport = propertySupport;
		propertySupport.register(this);
	}

	public String getName() {
		return name;
	}

	public TransmitMode getTransmitMode() {
		return transmitMode;
	}

	public void setTransmitMode(final TransmitMode transmitMode) {
		this.transmitMode = transmitMode;
	}

	public boolean mustSendToProperty() {
		return transmitMode.toProperty;
	}

	public boolean mustSendToComponent() {
		return transmitMode.toComponent;
	}

	public void attach() {
		setTransmitMode(TransmitMode.BOTH);
	}

	public void setErrorNotifier(final ErrorNotifier errorNotifier) {
		if (errorNotifier != null) {
			this.errorNotifier = errorNotifier;
		} else {
			this.errorNotifier = emptyErrorNotifier();
		}
	}

	public void addListener(final PropertyChangeListener propertyChangeListener) {
		propertySupport.addPropertyChangeListener(name, propertyChangeListener);
	}

	public void removeListener(final PropertyChangeListener propertyChangeListener) {
		propertySupport.getChangeSupport().removePropertyChangeListener(name, propertyChangeListener);
	}

	public void removeListeners(final List<IPropertyEventListener> toRemove) {
		toRemove.forEach(this::removeListener);
	}

	public void removeAllListeners() {
		propertySupport.getChangeSupport().removeAllPropertyChangeListener(name);
	}

	public boolean isModifiedBy(final Object caller) {
		return propertySupport.getChangeSupport().isModifiedBy(name, caller);
	}

	public void addListener(final IPropertyEventListener listener) {
		eventListeners.add(IPropertyEventListener.class, listener);
	}

	public void removeListener(final IPropertyEventListener listener) {
		eventListeners.remove(IPropertyEventListener.class, listener);
	}

	protected void onValueSet(final Object caller, final EventKind eventKind) {
		final var event = new PropertyEvent(eventKind, this);
		Stream.of(eventListeners.getListeners(IPropertyEventListener.class))
				.forEach(l -> l.propertyModified(caller, event));
	}

	@SafeVarargs
	public final AbstractProperty configure(@SuppressWarnings("unchecked") final Consumer<AbstractProperty>... properties) {
		Stream.of(properties).forEach(prop -> prop.accept(this));
		return this;
	}

	public void dispose() {
		// no op
	}

	@Override
	public String toString() {
		return "Property " + name;
	}

	@Override
	public final boolean equals(final Object obj) {
		return this.getClass().isInstance(obj) && name.equals(((AbstractProperty) obj).name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public String getModifiedBy() {
		return propertySupport.getChangeSupport().getModificationStack(getName());
	}

}
