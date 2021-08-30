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
package ch.skymarshall.gui.mvc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;

import ch.skymarshall.gui.mvc.Veto.TransmitMode;
import ch.skymarshall.gui.mvc.converters.ChainInhibitedException;
import ch.skymarshall.gui.mvc.converters.ConversionException;
import ch.skymarshall.gui.mvc.converters.GuiErrors;
import ch.skymarshall.gui.mvc.converters.IConverter;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;
import ch.skymarshall.gui.mvc.properties.AbstractProperty.ErrorNotifier;

/**
 * Holds and applies the bindings. Each chain listens to the property and to a
 * gui component
 *
 * @author scaille
 *
 */
public class BindingChain implements IBindingController {

	private interface Link {
		Object toComponent(Object value) throws ConversionException;

		Object toProperty(Object component, Object value) throws ConversionException;

		void unbind();
	}

	@FunctionalInterface
	private interface ConversionFunction {
		Object apply(Object value) throws ConversionException;
	}

	/**
	 * To enable / disable the binding
	 */
	private IVeto veto;

	/**
	 * All the links (converters, ...)
	 */
	private final List<Link> links = new ArrayList<>();

	private final AbstractProperty property;

	private final PropertyChangeListener valueUpdateListener;

	private final ErrorNotifier errorNotifier;

	private final List<IBindingChainDependency> dependencies = new ArrayList<>();

	public class EndOfChain<T> {

		private final class LinkToComponent implements Link {
			private final IComponentBinding<T> newBinding;

			private LinkToComponent(final IComponentBinding<T> newBinding) {
				this.newBinding = newBinding;

				newBinding.addComponentValueChangeListener(new IComponentLink<T>() {
					@Override
					public void setValueFromComponent(final Object component, final T componentValue) {
						if (veto != null && !veto.mustSendToProperty(BindingChain.this)) {
							return;
						}
						Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE, () -> "Component change: "
								+ component.getClass().getSimpleName() + ": " + componentValue);
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
				Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE, () -> "Setting component value: " + value);
				newBinding.setComponentValue(property, (T) value);
				return value;
			}

			@Override
			public Object toProperty(final Object component, final Object value) {
				return value;
			}

			@Override
			public void unbind() {
				newBinding.removeComponentValueChangeListener();
			}

			private void propagateComponentChange(final Object component, final Object componentValue) {
				final int pos = links.size();
				Object value = componentValue;
				for (int i = pos - 1; i >= 0; i--) {
					try {
						value = links.get(i).toProperty(component, value);
					} catch (final ChainInhibitedException e) {
						Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE,
								() -> "Component change inhibited: " + e.getMessage());
						return;
					} catch (final ConversionException e) {
						errorNotifier.notifyError(component, GuiErrors.fromException(getProperty(), e));
						return;
					}
				}
				errorNotifier.clearError(component, getProperty());
			}
		}

		public EndOfChain<T> addDependency(final IBindingChainDependency dependency) {
			BindingChain.this.addDependency(dependency);
			return this;
		}

		public IBindingController listen(final Consumer<T> newBinding) {
			links.add(link(v -> {
				newBinding.accept((T) v);
				return null;
			}, v -> {
				throw setOnlyException();
			}));
			return BindingChain.this;
		}

		private IllegalStateException setOnlyException() {
			return new IllegalStateException("Binding can only call setters");
		}

		public IBindingController bind(final IComponentBinding<T> newBinding) {
			links.add(new LinkToComponent(newBinding));
			ScreenBuildingReport.addDependency(property, newBinding);
			return BindingChain.this;
		}

		/**
		 * @param<N> type of the next converter
		 *
		 * @param link
		 * @return
		 */
		public <N> EndOfChain<N> bind(final IConverter<T, N> link) {
			link.initialize(getProperty());
			links.add(link(value -> link.convertPropertyValueToComponentValue((T) value),
					value -> link.convertComponentValueToPropertyValue((N) value)));
			return new EndOfChain<>();
		}

		/**
		 * @param N         next type
		 * @param prop2Comp
		 * @param comp2Prop
		 * @return
		 */
		public <N> EndOfChain<N> bind(final Function<T, N> prop2Comp, final Function<N, T> comp2Prop) {
			links.add(link(value -> prop2Comp.apply((T) value), value -> comp2Prop.apply((N) value)));
			return new EndOfChain<>();
		}

		/**
		 * @param N         next type
		 * @param prop2Comp
		 * @param comp2Prop
		 * @return
		 */
		public <N> EndOfChain<N> bind(final Function<T, N> prop2Comp) {
			links.add(link(value -> prop2Comp.apply((T) value), value -> {
				throw new ConversionException("Read only");
			}));
			return new EndOfChain<>();
		}

		private Link link(final ConversionFunction prop2Comp, final ConversionFunction comp2Prop) {
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
				public void unbind() {
					// nothing to do
				}

			};
		}

	}

	public BindingChain(final AbstractProperty prop, final ErrorNotifier errorNotifier) {
		this.property = prop;
		this.errorNotifier = errorNotifier;
		// handle property change
		this.valueUpdateListener = this::propagatePropertyChange;
	}

	private void propagatePropertyChange(final PropertyChangeEvent evt) {
		if (veto != null && !veto.mustSendToComponent(BindingChain.this)) {
			return;
		}
		Object value = evt.getNewValue();
		Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE, () -> "Property change: " + evt.getPropertyName() + ": "
				+ evt.getOldValue() + " -> " + evt.getNewValue());
		Logging.MVC_EVENTS_DEBUGGER.log(Level.FINEST, () -> Arrays.toString(new Exception().getStackTrace()));

		for (final Link link : links) {
			try {
				value = link.toComponent(value);
			} catch (final ChainInhibitedException e) {
				Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE, () -> "Property change inhibited: " + e.getMessage());
				return;
			} catch (final ConversionException e) {
				errorNotifier.notifyError(property, GuiErrors.fromException(getProperty(), e));
				return;
			}
		}
		errorNotifier.clearError(property, property);
	}

	public <T> EndOfChain<T> bindProperty(final BiConsumer<Object, T> propertySetter) {
		property.addListener(valueUpdateListener);
		links.add(new Link() {
			@Override
			public Object toProperty(final Object component, final Object value) {
				Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE, () -> "Setting property value: " + value);
				propertySetter.accept(component, (T) value);
				return null;
			}

			@Override
			public Object toComponent(final Object value) {
				return value;
			}

			@Override
			public void unbind() {
				// nothing to do
			}

		});
		return new EndOfChain<>();
	}

	@Override
	public void forceViewUpdate() {
		property.fireArtificialChange(this);
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
		links.stream().forEach(Link::unbind);
	}

	@Override
	public IVeto getVeto() {
		if (veto == null) {
			veto = new Veto(TransmitMode.BOTH);
		}
		return veto;
	}

	public void setVeto(final IVeto veto) {
		this.veto = veto;
	}

	public IBindingController addPropertyInhibitor(Predicate<BindingChain> inhibitor) {
		getVeto().inhibitTransmitToComponentWhen(inhibitor);
		return this;
	}

	@Override
	public String toString() {
		return "Chain of " + property.getName();
	}
}
