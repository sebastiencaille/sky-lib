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
package ch.scaille.gui.mvc.properties;

import java.util.function.Consumer;

import ch.scaille.gui.mvc.BindingChain;
import ch.scaille.gui.mvc.BindingChain.EndOfChain;
import ch.scaille.gui.mvc.GuiModel;
import ch.scaille.gui.mvc.IScopedSupport;

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

	public BooleanProperty(final String name, final GuiModel model, final boolean defaultValue) {
		super(name, model);
		this.defaultValue = defaultValue;
	}

	public BooleanProperty(final String name, final GuiModel model) {
		this(name, model, false);
	}

	public BooleanProperty(final String name, final IScopedSupport propertySupport, final boolean defaultValue) {
		super(name, propertySupport);
		this.defaultValue = defaultValue;
	}

	public BooleanProperty(final String name, final IScopedSupport propertySupport) {
		this(name, propertySupport, false);
	}

	@Override
	public EndOfChain<Boolean> createBindingChain() {
		return new BindingChain(this, errorNotifier).<Boolean>bindProperty(this::setObjectValueFromComponent);
	}

	@SafeVarargs
	@Override
	public final BooleanProperty configureTyped(final Consumer<AbstractTypedProperty<Boolean>>... propertyConfigurer) {
		super.configureTyped(propertyConfigurer);
		return this;
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
		return getValue();
	}

	@Override
	public void reset(final Object caller) {
		setValue(this, defaultValue);
	}

	@Override
	public void attach() {
		super.attach();
		propertySupport.getMain().firePropertyChange(getName(), this, null, value);
	}

}
