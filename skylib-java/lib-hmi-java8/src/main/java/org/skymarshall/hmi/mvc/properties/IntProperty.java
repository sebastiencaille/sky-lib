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

import org.skymarshall.hmi.mvc.BindingChain;
import org.skymarshall.hmi.mvc.BindingChain.EndOfChain;
import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.converters.AbstractIntConverter;

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

	@Override
	protected EndOfChain<Integer> createBindingChain() {
		return new BindingChain(this, errorNotifier).<Integer>bindProperty((c, v) -> setObjectValueFromComponent(c, v));
	}

	public <C> EndOfChain<C> bind(final AbstractIntConverter<C> binding) {
		return createBindingChain().bind(binding);
	}

	@SafeVarargs
	@Override
	public final IntProperty setTypedConfiguration(final Consumer<AbstractTypedProperty<Integer>>... propertyConfigurer) {
		super.setTypedConfiguration(propertyConfigurer);
		return this;
	}

	public int getValue() {
		return value;
	}

	public void setValue(final Object caller, final int newValue) {
		setObjectValue(caller, newValue);
	}

	@Override
	protected Integer replaceValue(final Integer newValue) {
		if (newValue == null) {
			throw new IllegalArgumentException("Null value is not allowed");
		}
		final int oldValue = value;
		value = newValue;
		return oldValue;
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
		setObjectValue(this, defaultValue);
	}

}
