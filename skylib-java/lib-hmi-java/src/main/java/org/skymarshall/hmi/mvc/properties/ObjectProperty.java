/*******************************************************************************
 * Copyright (c) 2013 Sebastien Caille.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.mvc.properties;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.IBindingController;
import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.PropertyEvent.EventKind;
import org.skymarshall.hmi.mvc.converters.AbstractObjectConverter;
import org.skymarshall.hmi.mvc.converters.IdentityObjectConverter;

/**
 * A property that contains an object.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class ObjectProperty<T> extends AbstractTypedProperty<T> {

	private T value;

	private T defaultValue;

	public ObjectProperty(final String name, final ControllerPropertyChangeSupport propertySupport,
			final T defaultValue) {
		super(name, propertySupport);
		this.defaultValue = defaultValue;
		value = defaultValue;
	}

	public ObjectProperty(final String name, final ControllerPropertyChangeSupport propertySupport) {
		this(name, propertySupport, null);
	}

	public <C> IBindingController<C> bind(final AbstractObjectConverter<T, C> converter) {
		return converter.bindWithProperty(this, errorNotifier);
	}

	public IBindingController<T> bind(final IComponentBinding<T> binding) {
		final IBindingController<T> controller = bind(new IdentityObjectConverter<T>());
		controller.bind(binding);
		return controller;
	}

	public void setValue(final Object caller, final T newValue) {
		onValueSet(caller, EventKind.BEFORE);
		try {
			final T oldValue = value;
			value = newValue;
			if (attached && (oldValue != null || newValue != null)) {
				propertySupport.firePropertyChange(getName(), caller, oldValue, newValue);
			}
		} finally {
			onValueSet(caller, EventKind.AFTER);
		}
	}

	@Override
	public T getObjectValue() {
		return getValue();
	}

	@Override
	public void setObjectValue(final Object caller, final T newValue) {
		setValue(caller, newValue);
	}

	public void forceChanged(final Object caller) {
		propertySupport.firePropertyChange(getName(), caller, null, getValue());
	}

	protected boolean valueEquals(final T newValue) {
		return newValue == value || (newValue != null && value != null && newValue.equals(value));
	}

	public T getValue() {
		return value;
	}

	@Override
	public void attach() {
		super.attach();
		propertySupport.firePropertyChange(getName(), this, null, value);
	}

	@Override
	public void reset(final Object caller) {
		setValue(this, defaultValue);
	}

}
