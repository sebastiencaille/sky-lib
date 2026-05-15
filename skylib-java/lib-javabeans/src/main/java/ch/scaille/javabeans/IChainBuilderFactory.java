package ch.scaille.javabeans;

import ch.scaille.javabeans.converters.IConverter;
import org.jspecify.annotations.Nullable;

/**
 *
 * @param <P> the property type
 */

public interface IChainBuilderFactory<P extends @Nullable Object> extends IChainBuilder<P> {

	<C extends @Nullable Object> IChainBuilderFactory<C> earlyBind(IConverter<P, C> conv);
	
}
