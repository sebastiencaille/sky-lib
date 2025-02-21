package ch.scaille.javabeans;

import ch.scaille.javabeans.converters.IConverter;

public interface IChainBuilderFactory<T> extends IChainBuilder<T, Object> {

	<K> IChainBuilder<T, K> withContext(K context);

	<C> IChainBuilderFactory<C> earlyBind(IConverter<T, C> conv);
	
}
