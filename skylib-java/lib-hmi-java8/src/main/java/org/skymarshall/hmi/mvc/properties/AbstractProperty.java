/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.mvc.properties;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.swing.event.EventListenerList;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.HmiErrors.HmiError;
import org.skymarshall.hmi.mvc.IPropertyEventListener;
import org.skymarshall.hmi.mvc.PropertyEvent;
import org.skymarshall.hmi.mvc.PropertyEvent.EventKind;

/**
 * Provides default type-independant mechanisms of properties.
 * <p>
 *
 * @author Sebastien Caille
 *
 */
public abstract class AbstractProperty {

	@FunctionalInterface
	public interface ErrorNotifier {
		void notifyError(Object source, HmiError error);
	};

	public static ErrorNotifier emptyErrorNotifier() {
		return (s, e) -> {
		};
	}

	/**
	 * Name of the property
	 */
	private final String name;

	/**
	 * Support to trigger property change
	 */
	protected final ControllerPropertyChangeSupport propertySupport;

	/**
	 * Property related events (before firing, after firing, ...)
	 */
	protected EventListenerList eventListeners = new EventListenerList();

	/**
	 * Error property
	 */
	protected ErrorNotifier errorNotifier = emptyErrorNotifier();

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
		toRemove.forEach(l -> removeListener(l));
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
	public boolean equals(final Object obj) {
		if (obj == null || !this.getClass().isInstance(obj)) {
			return false;
		}

		return name.equals(((AbstractProperty) obj).name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
