package ch.scaille.javabeans;

import ch.scaille.javabeans.converters.IConverter;
import org.jspecify.annotations.NullMarked;

/**
 *
 * @param <P> the property type
 */
@NullMarked
public interface IChainBuilderFactory<P> extends IChainBuilder<P> {

	<C> IChainBuilderFactory<C> earlyBind(IConverter<P, C> conv);
	
}
