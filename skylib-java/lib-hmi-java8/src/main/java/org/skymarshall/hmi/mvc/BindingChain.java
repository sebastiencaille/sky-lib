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
package org.skymarshall.hmi.mvc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.skymarshall.hmi.mvc.converters.ConversionException;
import org.skymarshall.hmi.mvc.converters.IConverter;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import org.skymarshall.hmi.mvc.properties.AbstractProperty.ErrorNotifier;

/**
 * Holds and applies the bindings. Each chain listens to the property and to a
 * hmi component
 *
 * @author scaille
 *
 */
public class BindingChain implements IBindingController {

	private interface Link {
		Object toComponent(Object value) throws ConversionException;

		Object toProperty(Object component, Object value) throws ConversionException;

		void handleException(ConversionException e);
	}

	@FunctionalInterface
	private interface ConversionFunction {
		Object apply(Object value) throws ConversionException;
	}

	private static Link link(final ConversionFunction prop2Comp, final ConversionFunction comp2Prop,
			final Consumer<ConversionException> exceptionConsumer) {
		return new Link() {

			@Override
			public Object toComponent(final Object value) throws ConversionException {
				return prop2Comp.apply(value);
			}

			@Override
			public Object toProperty(final Object component, final Object value) throws ConversionException {
				return comp2Prop.apply(value);
			}

			@Override
			public void handleException(final ConversionException e) {
				exceptionConsumer.accept(e);
			}

		};
	}

	/**
	 * All the links (converters, ...)
	 */
	private final List<Link> links = new ArrayList<>();

	private final AbstractProperty property;

	private final PropertyChangeListener valueUpdateListener;

	private boolean transmit = true;

	private final ErrorNotifier errorNotifier;

	private final List<IBindingChainDependency> dependencies = new ArrayList<>();

	public class EndOfChain<T> {

		public EndOfChain<T> addDependency(final IBindingChainDependency dependency) {
			BindingChain.this.addDependency(dependency);
			return this;
		}

		public IBindingController bindWO(final Consumer<T> newBinding) {
			links.add(link(v -> {
				newBinding.accept((T) v);
				return null;
			}, v -> {
				throw readOnlyException();
			}, e -> {
				throw readOnlyException();
			}));
			return BindingChain.this;
		}

		private IllegalStateException readOnlyException() {
			return new IllegalStateException("Read only");
		}

		public IBindingController bind(final IComponentBinding<T> newBinding) {
			links.add(new Link() {

				{
					newBinding.addComponentValueChangeListener(new IComponentLink<T>() {
						@Override
						public void setValueFromComponent(final Object component, final T componentValue) {
							if (!transmit) {
								return;
							}
							propagateComponentChange(component, componentValue);
						}

						@Override
						public void unbind() {
							newBinding.removeComponentValueChangeListener();
						}

						@Override
						public void reloadComponentValue() {
							// should trigger the listeners
							property.attach();
						}
					});
				}

				@Override
				public Object toComponent(final Object value) {
					newBinding.setComponentValue(property, (T) value);
					return value;
				}

				@Override
				public Object toProperty(final Object component, final Object value) {
					return value;
				}

				@Override
				public void handleException(final ConversionException e) {
					// nope
				}
			});
			property.attach();
			return BindingChain.this;
		}

		public <NextType> EndOfChain<NextType> bind(final IConverter<T, NextType> link) {
			links.add(link(value -> link.convertPropertyValueToComponentValue((T) value),
					value -> link.convertComponentValueToPropertyValue((NextType) value),
					e -> errorNotifier.notifyError(property, HmiErrors.fromException(e))));
			return new EndOfChain<>();
		}

		public <NextType> EndOfChain<NextType> bind(final Function<T, NextType> prop2Comp,
				final Function<NextType, T> comp2Prop) {
			links.add(link(value -> prop2Comp.apply((T) value), value -> comp2Prop.apply((NextType) value),
					e -> errorNotifier.notifyError(property, HmiErrors.fromException(e))));
			return new EndOfChain<>();
		}

		public <NextType> EndOfChain<NextType> bind(final Function<T, NextType> prop2Comp) {
			links.add(link(value -> prop2Comp.apply((T) value), value -> {
				throw new ConversionException("Read only");
			}, e -> errorNotifier.notifyError(property, HmiErrors.fromException(e))));
			return new EndOfChain<>();
		}

	}

	public BindingChain(final AbstractProperty prop, final ErrorNotifier errorNotifier) {
		this.property = prop;
		this.errorNotifier = errorNotifier;
		// handle property change
		this.valueUpdateListener = this::propagateProperyChange;
	}

	private void propagateProperyChange(final PropertyChangeEvent evt) {
		if (!transmit) {
			return;
		}
		Object value = evt.getNewValue();
		for (final Link link : links) {
			try {
				value = link.toComponent(value);
			} catch (final ConversionException e) {
				link.handleException(e);
				return;
			}
		}
	}

	private void propagateComponentChange(final Object component, final Object componentValue) {
		final int pos = links.size();
		Object value = componentValue;
		for (int i = pos - 1; i >= 0; i--) {
			try {
				value = links.get(i).toProperty(component, value);
			} catch (final ConversionException e) {
				links.get(i).handleException(e);
				return;
			}
		}
	}

	public <T> EndOfChain<T> bindProperty(final BiConsumer<Object, T> propertySetter) {
		property.addListener(valueUpdateListener);
		links.add(new Link() {
			@Override
			public Object toProperty(final Object component, final Object value) throws ConversionException {
				propertySetter.accept(component, (T) value);
				return null;
			}

			@Override
			public Object toComponent(final Object value) throws ConversionException {
				return value;
			}

			@Override
			public void handleException(final ConversionException e) {
				// no conversion
			}
		});
		return new EndOfChain<>();
	}

	@Override
	public void attach() {
		transmit = true;
		property.attach();
	}

	@Override
	public void detach() {
		transmit = false;
	}

	@Override
	public AbstractProperty getProperty() {
		return property;
	}

	@Override
	public IBindingController addDependency(final IBindingChainDependency dependency) {
		dependency.register(this);
		dependencies.add(dependency);
		return this;
	}

	@Override
	public void unbind() {
		property.removeListener(valueUpdateListener);
		dependencies.stream().forEach(IBindingChainDependency::unbind);
	}

}
