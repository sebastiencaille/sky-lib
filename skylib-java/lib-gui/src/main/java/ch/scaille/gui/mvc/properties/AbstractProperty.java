/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.scaille.gui.mvc.properties;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.swing.event.EventListenerList;

import ch.scaille.gui.mvc.GuiError;
import ch.scaille.gui.mvc.IPropertyEventListener;
import ch.scaille.gui.mvc.IPropertiesGroup;
import ch.scaille.gui.mvc.PropertyEvent;
import ch.scaille.gui.mvc.PropertyEvent.EventKind;
import ch.scaille.gui.mvc.Veto.TransmitMode;

/**
 * Provides default type-independant mechanisms of properties.
 * <p>
 *
 * @author Sebastien Caille
 *
 */
public abstract class AbstractProperty implements Serializable {

	public interface ErrorNotifier {
		void notifyError(Object source, GuiError error);

		void clearError(Object source, AbstractProperty property);

	}

	public static ErrorNotifier emptyErrorNotifier() {
		return new ErrorNotifier() {

			@Override
			public void notifyError(final Object source, final GuiError error) {
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
