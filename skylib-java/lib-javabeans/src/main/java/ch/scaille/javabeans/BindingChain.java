package ch.scaille.javabeans;

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

import ch.scaille.javabeans.Veto.TransmitMode;
import ch.scaille.javabeans.converters.ChainInhibitedException;
import ch.scaille.javabeans.converters.ConversionErrors;
import ch.scaille.javabeans.converters.ConversionException;
import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.javabeans.properties.AbstractProperty;
import ch.scaille.javabeans.properties.AbstractProperty.ErrorNotifier;

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

				newBinding.addComponentValueChangeListener(new IComponentLink<>() {
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
				final var pos = links.size();
				var value = componentValue;
				for (int i = pos - 1; i >= 0; i--) {
					try {
						value = links.get(i).toProperty(component, value);
					} catch (final ChainInhibitedException e) {
						Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE,
								() -> "Component change inhibited: " + e.getMessage());
						return;
					} catch (final ConversionException e) {
						errorNotifier.notifyError(component, ConversionErrors.fromException(getProperty(), e));
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
				throw prop2CompOnlyException();
			}));
			return BindingChain.this;
		}

		private IllegalStateException prop2CompOnlyException() {
			return new IllegalStateException("Binding cannot read component values");
		}

		public IBindingController bind(final IComponentBinding<T> newBinding) {
			links.add(new LinkToComponent(newBinding));
			DependenciesBuildingReport.addDependency(property, newBinding);
			return BindingChain.this;
		}

		/**
		 * @param <N> type of the next converter
		 */
		public <N> EndOfChain<N> bind(final IConverter<T, N> link) {
			link.initialize(getProperty());
			links.add(link(value -> link.convertPropertyValueToComponentValue((T) value),
					value -> link.convertComponentValueToPropertyValue((N) value)));
			return new EndOfChain<>();
		}

		/**
		 * @param <N> type of the next converter
		 */
		public <N> EndOfChain<N> bind(final Function<T, N> prop2Comp, final Function<N, T> comp2Prop) {
			links.add(link(value -> prop2Comp.apply((T) value), value -> comp2Prop.apply((N) value)));
			return new EndOfChain<>();
		}

		/**
		 * @param <N> type of the next converter
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
		var value = evt.getNewValue();
		Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE, () -> "Property change: " + evt.getPropertyName() + ": "
				+ evt.getOldValue() + " -> " + evt.getNewValue());
		Logging.MVC_EVENTS_DEBUGGER.log(Level.FINEST, () -> Arrays.toString(new Exception().getStackTrace()));

		for (final var link : links) {
			try {
				value = link.toComponent(value);
			} catch (final ChainInhibitedException e) {
				Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE, () -> "Property change inhibited: " + e.getMessage());
				return;
			} catch (final ConversionException e) {
				errorNotifier.notifyError(property, ConversionErrors.fromException(getProperty(), e));
				return;
			}
		}
		errorNotifier.clearError(property, property);
	}

	/**
	 * Binds to the property
	 * @param <T> the type of the property
	 * @param propertySetter the property setter that must be called then setting the value coming from the components
	 * @return an end of chain, to dynamically control the chain
	 */
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
		dependencies.forEach(IBindingChainDependency::unbind);
		links.forEach(Link::unbind);
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
