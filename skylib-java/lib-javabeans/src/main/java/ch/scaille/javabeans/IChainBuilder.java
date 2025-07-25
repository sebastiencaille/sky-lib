package ch.scaille.javabeans;

import java.util.function.Consumer;
import java.util.function.Function;

import ch.scaille.javabeans.converters.IConverter;

/**
 * 
 * @param <T> The current type
 */
public interface IChainBuilder<T> {

	IBindingController listen(Consumer<T> newBinding);

	IBindingController bind(IComponentBinding<T> newBinding);

	/**
	 * @param <N> type of the next co>nverter
	 */
	<N> IChainBuilder<N> bind(IConverter<T, N> converter);

	/**
	 * @param <N> type of the next converter
	 */
	<N> IChainBuilder<N> bind(Function<T, N> prop2Comp, Function<N, T> comp2Prop);

	/**
	 * @param <N> type of the next converter
	 */
	<N> IChainBuilder<N> bind(Function<T, N> prop2Comp);

}
