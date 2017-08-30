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

import java.util.Optional;
import java.util.function.Consumer;

import org.skymarshall.hmi.mvc.BindingChain;
import org.skymarshall.hmi.mvc.BindingChain.EndOfChain;
import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;

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

	@Override
	protected EndOfChain<T> createBindingChain() {
		return new BindingChain(this, errorNotifier).<T>bindProperty(this::setObjectValueFromComponent);
	}

	@Override
	@SafeVarargs
	public final ObjectProperty<T> setTypedConfiguration(final Consumer<AbstractTypedProperty<T>>... properties) {
		super.setTypedConfiguration(properties);
		return this;
	}

	@Override
	protected T replaceValue(final T newValue) {
		final T oldValue = value;
		value = newValue;
		return oldValue;
	}

	public void setValue(final Object caller, final T newValue) {
		setObjectValue(caller, newValue);
	}

	@Override
	public T getObjectValue() {
		return getValue();
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

	public Optional<T> optional() {
		return Optional.ofNullable(value);
	}

	@Override
	public void attach() {
		super.attach();
		propertySupport.firePropertyChange(getName(), this, null, getValue());
	}

	@Override
	public void reset(final Object caller) {
		setObjectValue(this, defaultValue);
	}

}
