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
package org.skymarshall.hmi.mvc.properties;

import org.skymarshall.hmi.mvc.BindingChain;
import org.skymarshall.hmi.mvc.BindingChain.EndOfChain;
import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;

/**
 * Property containing a boolean value.
 * <p>
 *
 * @author Sebastien Caille
 *
 */
public class BooleanProperty extends AbstractTypedProperty<Boolean> {
	private boolean value;
	private final boolean defaultValue;

	public BooleanProperty(final String name, final ControllerPropertyChangeSupport propertySupport,
			final boolean defaultValue) {
		super(name, propertySupport);
		this.defaultValue = defaultValue;
	}

	public BooleanProperty(final String name, final ControllerPropertyChangeSupport propertySupport) {
		this(name, propertySupport, false);
	}

	@Override
	protected EndOfChain<Boolean> createBindingChain() {
		return new BindingChain(this, errorNotifier).<Boolean>bindProperty(this::setObjectValueFromComponent);
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(final Object caller, final boolean newValue) {
		setObjectValue(caller, newValue);
	}

	@Override
	protected Boolean replaceValue(final Boolean newValue) {
		if (newValue == null) {
			throw new IllegalArgumentException("Null value is not allowed");
		}
		final boolean oldValue = value;
		value = newValue;
		return oldValue;
	}

	@Override
	public Boolean getObjectValue() {
		return Boolean.valueOf(getValue());
	}

	@Override
	public void reset(final Object caller) {
		setValue(this, defaultValue);
	}

	@Override
	public void attach() {
		super.attach();
		propertySupport.firePropertyChange(getName(), this, null, Boolean.valueOf(value));
	}

}
