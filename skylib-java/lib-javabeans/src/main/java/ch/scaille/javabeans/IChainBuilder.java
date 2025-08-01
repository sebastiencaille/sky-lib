package ch.scaille.javabeans;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.javabeans.converters.IContextualConverter;
import ch.scaille.javabeans.properties.ContextProperties;

/**
 * 
 * @param <P> The property side type
 */
public interface IChainBuilder<P> {

	IBindingController listen(Consumer<P> newBinding);

	IBindingController bind(IComponentBinding<P> newBinding);

	/**
	 * @param <C> The component side type
	 */
	<C> IChainBuilder<C> bind(IConverter<P, C> converter);

	/**
	 * @param <C> The component side type
	 */
	<C> IChainBuilder<C> bind(Function<P, C> prop2Comp, Function<C, P> comp2Prop);

	/**
	 * @param <C> The component side type
	 */
	<C> IChainBuilder<C> bind(Function<P, C> prop2Comp);

	/**
	 * @param <C> The component side type
	 */
	<C, K> IChainBuilder<C> bind(IContextualConverter<P, C, K> converter);

	/**
	 * @param <C> The component side type
	 */
	<C, K> IChainBuilder<C> bind(ContextProperties<K> multiProperties, BiFunction<P, K, C> prop2Comp, BiFunction<C, K, P> comp2Prop);

	/**
	 * @param <C> The component side type
	 */
	<C, K> IChainBuilder<C> bind(ContextProperties<K> multiProperties, BiFunction<P, K, C> prop2Comp);

}
