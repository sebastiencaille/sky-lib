package ch.scaille.javabeans;

import ch.scaille.javabeans.converters.IConverter;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface IChainBuilderFactory<P> extends IChainBuilder<P> {

	<C> IChainBuilderFactory<C> earlyBind(IConverter<P, C> conv);
	
}
