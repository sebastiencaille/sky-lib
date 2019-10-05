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

import ch.skymarshall.gui.mvc.BindingChain;
import ch.skymarshall.gui.mvc.ControllerPropertyChangeSupport;
import ch.skymarshall.gui.mvc.BindingChain.EndOfChain;

/**
 * Property containing a float value.
 * <p>
 *
 * @author Sebastien Caille
 *
 */
public class FloatProperty extends AbstractTypedProperty<Float> {

	private float value;
	private final float defaultValue;

	public FloatProperty(final String name, final ControllerPropertyChangeSupport propertySupport,
			final float defaultValue) {
		super(name, propertySupport);
		this.defaultValue = defaultValue;
	}

	public FloatProperty(final String name, final ControllerPropertyChangeSupport propertySupport) {
		this(name, propertySupport, 0.0f);
	}

	@Override
	protected EndOfChain<Float> createBindingChain() {
		return new BindingChain(this, errorNotifier).<Float>bindProperty(this::setObjectValueFromComponent);
	}

	public float getValue() {
		return value;
	}

	public void setValue(final Object caller, final float newValue) {
		setObjectValue(caller, newValue);
	}

	@Override
	protected Float replaceValue(final Float newValue) {
		if (newValue == null) {
			throw new IllegalArgumentException("Null value is not allowed");
		}
		final float oldValue = value;
		value = newValue;
		return oldValue;
	}

	@Override
	public Float getObjectValue() {
		return getValue();
	}

	@Override
	public void attach() {
		super.attach();
		propertySupport.firePropertyChange(getName(), this, null, Float.valueOf(value));
	}

	@Override
	public void reset(final Object caller) {
		setValue(this, defaultValue);
	}

}
