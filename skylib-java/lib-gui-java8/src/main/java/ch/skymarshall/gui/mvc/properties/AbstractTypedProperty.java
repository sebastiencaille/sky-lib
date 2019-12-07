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
import java.util.function.Function;
import java.util.stream.Stream;

import ch.skymarshall.gui.mvc.BindingChain.EndOfChain;
import ch.skymarshall.gui.mvc.IBindingController;
import ch.skymarshall.gui.mvc.IComponentBinding;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.PropertyEvent.EventKind;
import ch.skymarshall.gui.mvc.converters.IConverter;

/**
 * A property with a typed value.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> the type of the object contained int the property
 */
public abstract class AbstractTypedProperty<T> extends AbstractProperty {

	private transient IPersister<T> persister;

	public AbstractTypedProperty(final String name, final IScopedSupport propertySupport) {
		super(name, propertySupport);
	}

	public void setPersister(final IPersister<T> persister) {
		this.persister = persister;
	}

	@Override
	public void load(final Object caller) {
		if (persister != null) {
			setObjectValue(caller, persister.get());
		}
	}

	@Override
	public void save() {
		try {
			persister.set(getObjectValue());
		} catch (final RuntimeException e) {
			throw new IllegalStateException("Failed to set property " + getName(), e);
		}
	}

	public void setObjectValueFromComponent(final Object caller, final T newValue) {
		if (attached) {
			setObjectValue(caller, newValue);
		}
	}

	public AbstractTypedProperty<T> setTypedConfiguration(
			@SuppressWarnings("unchecked") final Consumer<AbstractTypedProperty<T>>... propertyConfigurer) {
		Stream.of(propertyConfigurer).forEach(prop -> prop.accept(this));
		return this;
	}

	public <C> EndOfChain<C> bind(final IConverter<T, C> binding) {
		return createBindingChain().bind(binding);
	}

	public <C> EndOfChain<C> bind(final Function<T, C> binding) {
		return createBindingChain().bind(binding);
	}

	public IBindingController bind(final IComponentBinding<T> binding) {
		return createBindingChain().bind(binding);
	}

	public IBindingController listen(final Consumer<T> binding) {
		return createBindingChain().listen(binding);
	}

	protected void setObjectValue(final Object caller, final T newValue) {
		if (!attached) {
			replaceValue(newValue);
			return;
		}
		onValueSet(caller, EventKind.BEFORE);
		try {
			final T oldValue = replaceValue(newValue);
			if (oldValue != null || newValue != null) {
				propertySupport.getMain().firePropertyChange(getName(), caller, oldValue, newValue);
			}
		} finally {
			onValueSet(caller, EventKind.AFTER);
		}
	}

	@Override
	public void fireArtificialChange(final Object caller) {
		propertySupport.getMain().firePropertyChange(getName(), caller, null, getObjectValue());
	}

	protected abstract T replaceValue(T newValue);

	public abstract T getObjectValue();

	protected abstract EndOfChain<T> createBindingChain();

}
