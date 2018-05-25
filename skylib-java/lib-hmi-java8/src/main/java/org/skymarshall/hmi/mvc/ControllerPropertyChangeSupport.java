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
package org.skymarshall.hmi.mvc;

import static java.util.Arrays.stream;

import java.awt.EventQueue;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Deque;
import java.util.ArrayDeque;

import org.skymarshall.hmi.mvc.properties.AbstractProperty;

/**
 * PropertyChangeSupport adapted to the property controllers.
 * <p>
 *
 * The support can detect loops that may appear when a chain of properties are
 * fired.
 * <p>
 * By default, all the properties are detached from the controller. They can be
 * attached by calling the method startController
 *
 * @author Sebastien Caille
 *
 */
public class ControllerPropertyChangeSupport {

	private final PropertyChangeSupport support;

	private static class CallInfo {

		private final Object caller;
		private final Object newValue;

		public CallInfo(final Object caller, final Object newValue) {
			this.caller = caller;
			this.newValue = newValue;
		}

		@Override
		public String toString() {
			return caller + "->" + newValue;
		}

	}

	/** Information about properties currently called */
	private final Map<String, Deque<CallInfo>> callInfo = new HashMap<>();

	/** All the properties */
	private final Collection<AbstractProperty> properties = new ArrayList<>();

	/**
	 * If true, swing thread will be checked
	 */
	private final boolean checkSwingThread;

	public ControllerPropertyChangeSupport(final Object bean) {
		this(bean, true);
	}

	public ControllerPropertyChangeSupport(final Object bean, final boolean checkSwingThread) {
		this.checkSwingThread = checkSwingThread;
		support = new PropertyChangeSupport(bean);
	}

	private void endFire(final String propertyName) {
		final Deque<CallInfo> info = callInfo.get(propertyName);
		info.pop();
		if (info.isEmpty()) {
			callInfo.remove(propertyName);
		}
	}

	private void prepareFire(final String propertyName, final Object caller, final Object newValue) {
		if (checkSwingThread && !EventQueue.isDispatchThread()) {
			throw new IllegalStateException("Property " + propertyName + " fired out of Swing thread");
		}
		final Deque<CallInfo> info = callInfo.computeIfAbsent(propertyName, k -> new ArrayDeque<>(5));

		if (info.size() > 5) {
			final StringBuilder stack = new StringBuilder();
			info.stream().forEach(i -> stack.append(i).append(";"));
			throw new IllegalStateException(propertyName + " is already fired:" + stack.toString());
		}
		info.push(new CallInfo(caller, newValue));
	}

	public boolean isBeingFired(final String propertyName) {
		return propertyName.contains(propertyName);
	}

	public boolean isModifiedBy(final String name, final Object caller) {
		final Deque<CallInfo> info = callInfo.get(name);
		if (info == null) {
			return false;
		}
		return info.stream().anyMatch(i -> i.caller == caller);
	}

	public void addPropertyChangeListener(final String name, final PropertyChangeListener propertyChangeListener) {
		support.addPropertyChangeListener(name, propertyChangeListener);
	}

	public void removePropertyChangeListener(final String name, final PropertyChangeListener propertyChangeListener) {
		support.removePropertyChangeListener(name, propertyChangeListener);
	}

	public void removeAllPropertyChangeListener(final String name) {
		stream(support.getPropertyChangeListeners(name)).forEach(support::removePropertyChangeListener);
	}

	/**
	 * Triggers a property change
	 *
	 * @param propertyName name of the property
	 * @param caller       identification of the method's caller
	 * @param oldValue     the old value of the property
	 * @param newValue     the new value of the property
	 */
	public void firePropertyChange(final String propertyName, final Object caller, final Object oldValue,
			final Object newValue) {
		if (Objects.equals(oldValue, newValue)) {
			return;
		}
		prepareFire(propertyName, caller, newValue);
		try {
			support.firePropertyChange(propertyName, oldValue, newValue);
		} finally {
			endFire(propertyName);
		}
	}

	/**
	 * Registers a property
	 *
	 * @param abstractProperty the property to register
	 */
	public void register(final AbstractProperty abstractProperty) {
		properties.add(abstractProperty);
	}

	/**
	 * Attaches all the properties to the bindings. Should be called once all the
	 * components are bound to the properties
	 */
	public void attachAll() {
		for (final AbstractProperty property : properties) {
			property.attach();
		}
	}

}
