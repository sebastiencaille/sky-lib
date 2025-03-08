package ch.scaille.javabeans;

import java.util.function.Consumer;
import java.util.function.Function;

import ch.scaille.javabeans.converters.IConverter;
import ch.scaille.javabeans.converters.IConverterWithContext;

/**
 * 
 * @param <T> The current type
 * @param <K> The context type
 */
public interface IChainBuilder<T, K> {

	IBindingController listen(Consumer<T> newBinding);

	IBindingController bind(IComponentBinding<T> newBinding);

	/**
	 * @param <N> type of the next co>nverter
	 */
	<N> IChainBuilder<N, K> bind(IConverterWithContext<T, N, K> converter);

	/**
	 * @param <N> type of the next converter
	 */
	default <N> IChainBuilder<N, K> bind(IConverter<T, N> converter) {
		return bind(Converters.wrap(converter));
	}
	
	/**
	 * @param <N> type of the next converter
	 */
	<N> IChainBuilder<N, K> bind(Function<T, N> prop2Comp, Function<N, T> comp2Prop);

	/**
	 * @param <N> type of the next converter
	 */
	<N> IChainBuilder<N, K> bind(Function<T, N> prop2Comp);

}
