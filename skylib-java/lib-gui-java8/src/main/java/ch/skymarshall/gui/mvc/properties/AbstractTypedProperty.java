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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import ch.skymarshall.gui.mvc.BindingChain.EndOfChain;
import ch.skymarshall.gui.mvc.GuiModel;
import ch.skymarshall.gui.mvc.IBindingController;
import ch.skymarshall.gui.mvc.IComponentBinding;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.PropertyEvent.EventKind;
import ch.skymarshall.gui.mvc.Veto.TransmitMode;
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

	private final List<IConverter<T, T>> implicitConverters = new ArrayList<>();

	private transient IPersister<T> persister;

	protected AbstractTypedProperty(final String name, final GuiModel model) {
		super(name, model.getPropertySupport());
		setErrorNotifier(model.getErrorProperty());
	}

	protected AbstractTypedProperty(final String name, final IScopedSupport propertySupport) {
		super(name, propertySupport);
	}

	public void setPersister(final IPersister<T> persister) {
		this.persister = persister;
	}

	public void addImplicitConverter(IConverter<T, T> converter) {
		implicitConverters.add(converter);
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
		if (mustSendToProperty()) {
			setObjectValue(caller, newValue);
		}
	}

	public AbstractTypedProperty<T> configureTyped(final Consumer<AbstractTypedProperty<T>>... propertyConfigurer) {
		Stream.of(propertyConfigurer).forEach(prop -> prop.accept(this));
		return this;
	}

	public <C> EndOfChain<C> bind(final IConverter<T, C> binding) {
		return createBindingChainWithConv().bind(binding);
	}

	public <C> EndOfChain<C> bind(final Function<T, C> binding) {
		return createBindingChainWithConv().bind(binding);
	}

	public IBindingController bind(final IComponentBinding<T> binding) {
		return createBindingChainWithConv().bind(binding);
	}

	private EndOfChain<T> createBindingChainWithConv() {
		EndOfChain<T> chain = createBindingChain();
		for (IConverter<T, T> conv : implicitConverters) {
			chain = chain.bind(conv);
		}
		return chain;
	}

	/**
	 * Executes binding when the property is updated (transmitMode = BOTH only)
	 * 
	 * @param binding
	 * @return
	 */
	public IBindingController listenActive(final Consumer<T> binding) {
		IBindingController listen = createBindingChain().listen(binding);
		listen.getVeto().inhibitTransmitToComponentWhen(b -> b.getProperty().getTransmitMode() != TransmitMode.BOTH);
		return listen;
	}

	/**
	 * Executes binding when the property is updated (transmitMode =
	 * BOTH|TO_COMPONENT)
	 * 
	 * @param binding
	 * @return
	 */
	public IBindingController listen(final Consumer<T> binding) {
		return createBindingChain().listen(binding);
	}

	protected void setObjectValue(final Object caller, final T newValue) {
		onValueSet(caller, EventKind.BEFORE);
		try {
			if (!mustSendToComponent()) {
				replaceValue(newValue);
				return;
			}
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

	public abstract EndOfChain<T> createBindingChain();

}
