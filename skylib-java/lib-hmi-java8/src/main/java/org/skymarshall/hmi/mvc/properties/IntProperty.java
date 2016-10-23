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

import java.util.function.Consumer;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.IBindingController;
import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.PropertyEvent.EventKind;
import org.skymarshall.hmi.mvc.converters.AbstractIntConverter;
import org.skymarshall.hmi.mvc.converters.AbstractObjectConverter;
import org.skymarshall.hmi.mvc.converters.Converters;

/**
 * Property containing an int value.
 * <p>
 *
 * @author Sebastien Caille
 *
 */
public class IntProperty extends AbstractTypedProperty<Integer> {

	private int value;
	private final int defaultValue;

	public IntProperty(final String name, final ControllerPropertyChangeSupport propertySupport,
			final int defaultValue) {
		super(name, propertySupport);
		this.defaultValue = defaultValue;
	}

	public IntProperty(final String name, final ControllerPropertyChangeSupport propertySupport) {
		this(name, propertySupport, 0);
	}

	public <C> IBindingController<C> bind(final AbstractIntConverter<C> converter) {
		return converter.bindWithProperty(this, errorNotifier);
	}

	public <C> IBindingController<C> bind(final AbstractObjectConverter<Integer, C> converter) {
		return converter.bindWithProperty(this, errorNotifier);
	}

	public IBindingController<Integer> bind(final IComponentBinding<Integer> binding) {
		final IBindingController<Integer> controller = Converters.intIdentity().bindWithProperty(this, errorNotifier);
		controller.bind(binding);
		return controller;
	}

	@SafeVarargs
	@Override
	public final IntProperty setTypedConfiguration(final Consumer<AbstractTypedProperty<Integer>>... properties) {
		super.setTypedConfiguration(properties);
		return this;
	}

	public void setValue(final Object caller, final int newValue) {
		if (!attached) {
			return;
		}
		onValueSet(caller, EventKind.BEFORE);
		try {
			final int oldValue = value;
			value = newValue;
			propertySupport.firePropertyChange(getName(), caller, Integer.valueOf(oldValue), Integer.valueOf(newValue));
		} finally {
			onValueSet(caller, EventKind.AFTER);
		}
	}

	public int getValue() {
		return value;
	}

	@Override
	public void setObjectValue(final Object caller, final Integer newValue) {
		if (newValue == null) {
			throw new IllegalArgumentException("Null value is not allowed");
		}
		setValue(caller, newValue.intValue());
	}

	@Override
	public Integer getObjectValue() {
		return value;
	}

	@Override
	public void attach() {
		super.attach();
		propertySupport.firePropertyChange(getName(), this, null, Integer.valueOf(value));
	}

	@Override
	public void reset(final Object caller) {
		setValue(this, defaultValue);
	}

}
