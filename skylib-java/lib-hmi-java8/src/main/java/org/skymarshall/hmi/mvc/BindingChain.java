package org.skymarshall.hmi.mvc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.skymarshall.hmi.mvc.converters.AbstractConverter;
import org.skymarshall.hmi.mvc.converters.ConversionException;
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

	private final List<Link> links = new ArrayList<>();

	private final AbstractProperty property;

	private final PropertyChangeListener listener;

	private boolean transmit = true;

	private IPropertyEventListener detachReattachListener;

	private final ErrorNotifier errorNotifier;

	public class EndOfChain<T> {

		public EndOfChain<T> detachOnUpdateOf(final AbstractProperty prop) {
			BindingChain.this.detachOnUpdateOf(prop);
			return this;
		}

		public IBindingController bind(final IComponentBinding<T> newBinding) {
			links.add(new Link() {

				private final int pos = links.size();

				{
					newBinding.addComponentValueChangeListener(new IComponentLink<T>() {
						@Override
						public void setValueFromComponent(final Object component, final T componentValue) {
							if (!transmit) {
								return;
							}
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

						@Override
						public void unbind() {
						}

						@Override
						public void reloadComponentValue() {

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
					newBinding.setComponentValue(property, (T) value);
					return value;
				}

				@Override
				public void handleException(final ConversionException e) {
					// nope
				}
			});
			return BindingChain.this;
		}

		public <NextType> EndOfChain<NextType> bind(final AbstractConverter<T, NextType> link) {
			links.add(new Link() {
				@Override
				public Object toComponent(final Object value) throws ConversionException {
					return link.convertPropertyValueToComponentValue((T) value);
				}

				@Override
				public Object toProperty(final Object component, final Object value) throws ConversionException {
					return link.convertComponentValueToPropertyValue((NextType) value);
				}

				@Override
				public void handleException(final ConversionException e) {
					errorNotifier.notifyError(property, HmiErrors.fromException(e));
				}
			});
			return new EndOfChain<>();
		}
	}

	public BindingChain(final AbstractProperty prop, final ErrorNotifier errorNotifier) {
		this.property = prop;
		this.errorNotifier = errorNotifier;
		this.listener = new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
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
		};
		this.detachReattachListener = new IPropertyEventListener() {

			@Override
			public void propertyModified(final Object caller, final PropertyEvent event) {
				switch (event.getKind()) {
				case BEFORE:
					detach();
					break;
				case AFTER:
					attach();
					break;
				default:
					// ignore
					break;
				}

			}
		};
	}

	public <T> EndOfChain<T> bindProperty(final BiConsumer<Object, T> propertySetter) {
		property.addListener(listener);
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
	public void detachOnUpdateOf(final AbstractProperty property) {
		property.addListener(detachReattachListener);
	}

	@Override
	public void unbind() {

	}

}
