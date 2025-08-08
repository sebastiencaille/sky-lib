package ch.scaille.javabeans.chain;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

import ch.scaille.javabeans.properties.ContextProperties;
import ch.scaille.javabeans.DependenciesBuildingReport;
import ch.scaille.javabeans.IBindingController;
import ch.scaille.javabeans.IChainBuilder;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.Logging;
import ch.scaille.javabeans.converters.ConversionException;
import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.javabeans.converters.IContextualConverter;

/**
 * Chain builder
 * 
 * @param <P> The property side type
 */
public class EndOfChain<P> implements IChainBuilder<P> {
	
	/**
	 * Link that targets the component
	 */
	private final class LinkToComponent implements Link {
		private final IComponentBinding<P> newBinding;

		private LinkToComponent(final IComponentBinding<P> newBinding) {
			this.newBinding = newBinding;

			newBinding.addComponentValueChangeListener(new IComponentLink<>() {
				@Override
				public void setValueFromComponent(final Object component, final P componentValue) {
					if (!chain.mustSendToProperty(chain)) {
						return;
					}
					Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE,
							() -> "Component change: " + component.getClass().getSimpleName() + ": " + componentValue);
					chain.propagateComponentChange(component, componentValue);
				}

				@Override
				public void unbind() {
					newBinding.removeComponentValueChangeListener();
				}

				@Override
				public void reloadComponentValue() {
					chain.transmitChangesBothWays();
				}
			});
		}

		@Override
		public Object toComponent(final Object value) {
			Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE, () -> "Setting component value: " + value);
			newBinding.setComponentValue(chain.getProperty(), (P) value);
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

	}

	protected final IBindingChainModifier chain;
	
	public EndOfChain(IBindingChainModifier chain) {
		this.chain = chain;
	}

	private IllegalStateException prop2CompOnlyException() {
		return new IllegalStateException("Binding cannot read component values");
	}

	@Override
	public IBindingController listen(final Consumer<P> newBinding) {
		chain.addLink(link(v -> {
			newBinding.accept((P) v);
			return null;
		}, v -> {
			throw prop2CompOnlyException();
		}));
		return chain;
	}

	@Override
	public IBindingController bind(final IComponentBinding<P> newBinding) {
		chain.addLink(new LinkToComponent(newBinding));
		DependenciesBuildingReport.addDependency(chain.getProperty(), newBinding);
		return chain;
	}

	/**
	 * @param <C> The component side type
	 */
	@Override
	public <C> EndOfChain<C> bind(final IConverter<P, C> converter) {
		converter.initialize(chain.getProperty());
		chain.addLink(link(value -> converter.convertPropertyValueToComponentValue((P) value),
				value -> converter.convertComponentValueToPropertyValue((C) value)));
		return new EndOfChain<>(chain);
	}

	/**
	 * @param <C> The component side type
	 */
	@Override
	public <C> EndOfChain<C> bind(final Function<P, C> prop2Comp, final Function<C, P> comp2Prop) {
		chain.addLink(link(value -> prop2Comp.apply((P) value), value -> comp2Prop.apply((C) value)));
		return new EndOfChain<>(chain);
	}

	/**
	 * @param <C> The component side type
	 */
	@Override
	public <C> EndOfChain<C> bind(final Function<P, C> prop2Comp) {
		chain.addLink(link(value -> prop2Comp.apply((P) value), value -> {
			throw new ConversionException("Read only");
		}));
		return new EndOfChain<>(chain);
	}

	/**
	 * @param <C> The component side type
	 */
	@Override
	public <C, K> EndOfChain<C> bind(final IContextualConverter<P, C, K> converter) {
		converter.initialize(chain.getProperty());
		final var contextProperties = converter.contextProperties();
		register(contextProperties);
		chain.addLink(link(value -> converter.convertPropertyValueToComponentValue((P) value, contextProperties.object()),
				value -> converter.convertComponentValueToPropertyValue((C) value, contextProperties.object())));
		return new EndOfChain<>(chain);
	}

	/**
	 * @param <C> The component side type
	 */
	@Override
	public <C, K> EndOfChain<C> bind(final ContextProperties<K> multiProperties, final BiFunction<P, K, C> prop2Comp, final BiFunction<C, K, P> comp2Prop) {
		register(multiProperties);
		chain.addLink(link(value -> prop2Comp.apply((P) value, multiProperties.object()), value -> comp2Prop.apply((C) value, multiProperties.object())));
		return new EndOfChain<>(chain);
	}

	/**
	 * @param <C> The component side type
	 */
	@Override
	public <C, K> EndOfChain<C> bind(final ContextProperties<K> multiProperties, final BiFunction<P, K, C> prop2Comp) {
		register(multiProperties);
		chain.addLink(link(value -> prop2Comp.apply((P) value, multiProperties.object()), value -> {
			throw new ConversionException("Read only");
		}));
		return new EndOfChain<>(chain);
	}


	private void register(ContextProperties<?> multiProperties) {
		multiProperties.properties().forEach(p -> p.addListener(e -> chain.flushChanges()));
	}

	
	protected Link link(final ConversionFunction prop2Comp, final ConversionFunction comp2Prop) {
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