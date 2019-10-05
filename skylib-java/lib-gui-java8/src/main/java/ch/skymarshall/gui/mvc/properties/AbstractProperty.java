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
package ch.skymarshall.gui.mvc.properties;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.swing.event.EventListenerList;

import ch.skymarshall.gui.mvc.ControllerPropertyChangeSupport;
import ch.skymarshall.gui.mvc.IPropertyEventListener;
import ch.skymarshall.gui.mvc.PropertyEvent;
import ch.skymarshall.gui.mvc.GuiErrors.GuiError;
import ch.skymarshall.gui.mvc.PropertyEvent.EventKind;

/**
 * Provides default type-independant mechanisms of properties.
 * <p>
 *
 * @author Sebastien Caille
 *
 */
public abstract class AbstractProperty implements Serializable {

	@FunctionalInterface
	public interface ErrorNotifier {
		void notifyError(Object source, GuiError error);
	}

	public static ErrorNotifier emptyErrorNotifier() {
		return (s, e) -> { // nope
		};
	}

	/**
	 * Name of the property
	 */
	private final String name;

	/**
	 * Support to trigger property change
	 */
	protected final transient ControllerPropertyChangeSupport propertySupport;

	/**
	 * Property related events (before firing, after firing, ...)
	 */
	protected transient EventListenerList eventListeners = new EventListenerList();

	/**
	 * Error property
	 */
	protected transient ErrorNotifier errorNotifier = emptyErrorNotifier();

	protected boolean attached = false;

	public abstract void reset(final Object caller);

	public abstract void load(final Object caller);

	public abstract void save();

	public AbstractProperty(final String name, final ControllerPropertyChangeSupport propertySupport) {
		this.name = name;
		this.propertySupport = propertySupport;
		propertySupport.register(this);
	}

	public String getName() {
		return name;
	}

	public void detach() {
		attached = false;
	}

	public void attach() {
		attached = true;
	}

	public boolean isAttached() {
		return attached;
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
		propertySupport.removePropertyChangeListener(name, propertyChangeListener);
	}

	public void removeListeners(final List<IPropertyEventListener> toRemove) {
		toRemove.forEach(this::removeListener);
	}

	public void removeAllListeners() {
		propertySupport.removeAllPropertyChangeListener(name);
	}

	public boolean isModifiedBy(final Object caller) {
		return propertySupport.isModifiedBy(name, caller);
	}

	public void addListener(final IPropertyEventListener listener) {
		eventListeners.add(IPropertyEventListener.class, listener);
	}

	public void removeListener(final IPropertyEventListener listener) {
		eventListeners.remove(IPropertyEventListener.class, listener);
	}

	protected void onValueSet(final Object caller, final EventKind eventKind) {
		final PropertyEvent event = new PropertyEvent(eventKind, this);
		Stream.of(eventListeners.getListeners(IPropertyEventListener.class))
				.forEach(l -> l.propertyModified(caller, event));
	}

	public AbstractProperty setConfiguration(
			@SuppressWarnings("unchecked") final Consumer<AbstractProperty>... properties) {
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
		return obj != null && this.getClass().isInstance(obj) && name.equals(((AbstractProperty) obj).name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
