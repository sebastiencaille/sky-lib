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

import java.util.function.Consumer;

import ch.skymarshall.gui.mvc.BindingChain;
import ch.skymarshall.gui.mvc.BindingChain.EndOfChain;
import ch.skymarshall.gui.mvc.IScopedSupport;

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

	public LongProperty(final String name, final IScopedSupport propertySupport, final long defaultValue) {
		super(name, propertySupport);
		this.defaultValue = defaultValue;
	}

	public LongProperty(final String name, final IScopedSupport propertySupport) {
		this(name, propertySupport, 0);
	}

	@Override
	public EndOfChain<Long> createBindingChain() {
		return new BindingChain(this, errorNotifier).<Long>bindProperty(this::setObjectValueFromComponent);
	}

	@SafeVarargs
	@Override
	public final LongProperty configureTyped(final Consumer<AbstractTypedProperty<Long>>... propertyConfigurer) {
		super.configureTyped(propertyConfigurer);
		return this;
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
		propertySupport.getMain().firePropertyChange(getName(), this, null, Long.valueOf(value));
	}

	@Override
	public void reset(final Object caller) {
		setObjectValue(this, defaultValue);
	}

}
