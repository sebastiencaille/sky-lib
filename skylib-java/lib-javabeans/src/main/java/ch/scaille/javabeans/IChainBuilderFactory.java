package ch.scaille.javabeans;

import ch.scaille.javabeans.converters.IConverter;

public interface IChainBuilderFactory<T> extends IChainBuilder<T> {

	<C> IChainBuilderFactory<C> earlyBind(IConverter<T, C> conv);
	
}
