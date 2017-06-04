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

import org.skymarshall.hmi.mvc.BindingChain;
import org.skymarshall.hmi.mvc.BindingChain.EndOfChain;
import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.converters.AbstractLongConverter;

/**
 * Property containing a long value.
 * <p>
 *
 * @author Sebastien Caille
 *
 */
public class LongProperty extends AbstractTypedProperty<Long> {

	private long value;
	private final long defaultValue;

	public LongProperty(final String name, final ControllerPropertyChangeSupport propertySupport,
			final long defaultValue) {
		super(name, propertySupport);
		this.defaultValue = defaultValue;
	}

	public LongProperty(final String name, final ControllerPropertyChangeSupport propertySupport) {
		this(name, propertySupport, 0);
	}

	@Override
	protected EndOfChain<Long> createBindingChain() {
		return new BindingChain(this, errorNotifier).<Long>bindProperty((c, v) -> setObjectValueFromComponent(c, v));
	}

	public <C> EndOfChain<C> bind(final AbstractLongConverter<C> binding) {
		return createBindingChain().bind(binding);
	}

	public long getValue() {
		return value;
	}

	public void setValue(final Object caller, final long newValue) {
		setObjectValue(caller, newValue);
	}

	@Override
	protected Long replaceValue(final Long newValue) {
		if (newValue == null) {
			throw new IllegalArgumentException("Null value is not allowed");
		}
		final long oldValue = value;
		value = newValue;
		return oldValue;
	}

	@Override
	public Long getObjectValue() {
		return Long.valueOf(getValue());
	}

	@Override
	public void attach() {
		super.attach();
		propertySupport.firePropertyChange(getName(), this, null, Long.valueOf(value));
	}

	@Override
	public void reset(final Object caller) {
		setObjectValue(this, defaultValue);
	}

}
