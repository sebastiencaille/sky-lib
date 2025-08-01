package ch.scaille.javabeans;

import ch.scaille.javabeans.converters.IConverter;

public interface IChainBuilderFactory<P> extends IChainBuilder<P> {

	<C> IChainBuilderFactory<C> earlyBind(IConverter<P, C> conv);
	
}
