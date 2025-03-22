package ch.scaille.javabeans.chain;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

import ch.scaille.javabeans.DependenciesBuildingReport;
import ch.scaille.javabeans.IBindingController;
import ch.scaille.javabeans.IChainBuilder;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.Logging;
import ch.scaille.javabeans.converters.ConversionException;
import ch.scaille.javabeans.converters.IConverterWithContext;

/**
 * Chain builder
 * 
 * @param <T>
 * @param <K>
 */
public class EndOfChain<T, K> implements IChainBuilder<T, K> {
	
	/**
	 * Link that targets the component
	 */
	private final class LinkToComponent implements Link {
		private final IComponentBinding<T> newBinding;

		private LinkToComponent(final IComponentBinding<T> newBinding) {
			this.newBinding = newBinding;

			newBinding.addComponentValueChangeListener(new IComponentLink<>() {
				@Override
				public void setValueFromComponent(final Object component, final T componentValue) {
					if (chain.getVetoer() != null && !chain.mustSendToProperty(chain)) {
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
					// should trigger the listeners
					chain.getProperty().attach();
				}
			});
		}

		@Override
		public Object toComponent(final Object value) {
			Logging.MVC_EVENTS_DEBUGGER.log(Level.FINE, () -> "Setting component value: " + value);
			newBinding.setComponentValue(chain.getProperty(), (T) value);
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
	
	protected final K context;
	
	public EndOfChain(IBindingChainModifier chain, K context) {
		this.chain = chain;
		this.context = context;
	}

	private IllegalStateException prop2CompOnlyException() {
		return new IllegalStateException("Binding cannot read component values");
	}

	@Override
	public IBindingController listen(final Consumer<T> newBinding) {
		chain.addLink(link(v -> {
			newBinding.accept((T) v);
			return null;
		}, v -> {
			throw prop2CompOnlyException();
		}));
		return chain;
	}

	@Override
	public IBindingController bind(final IComponentBinding<T> newBinding) {
		chain.addLink(new LinkToComponent(newBinding));
		DependenciesBuildingReport.addDependency(chain.getProperty(), newBinding);
		return chain;
	}

	/**
	 * @param <N> type of the next converter
	 */
	@Override
	public <N> EndOfChain<N, K> bind(final IConverterWithContext<T, N, K> converter) {
		converter.initialize(chain.getProperty());
		chain.addLink(link(value -> converter.convertPropertyValueToComponentValue((T) value, context),
				value -> converter.convertComponentValueToPropertyValue((N) value, context)));
		return new EndOfChain<>(chain, context);
	}

	/**
	 * @param <N> type of the next converter
	 */
	@Override
	public <N> EndOfChain<N, K> bind(final Function<T, N> prop2Comp, final Function<N, T> comp2Prop) {
		chain.addLink(link(value -> prop2Comp.apply((T) value), value -> comp2Prop.apply((N) value)));
		return new EndOfChain<>(chain, context);
	}

	/**
	 * @param <N> type of the next converter
	 */
	@Override
	public <N> EndOfChain<N, K> bind(final Function<T, N> prop2Comp) {
		chain.addLink(link(value -> prop2Comp.apply((T) value), value -> {
			throw new ConversionException("Read only");
		}));
		return new EndOfChain<>(chain, context);
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