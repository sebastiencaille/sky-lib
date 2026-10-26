package ch.scaille.javabeans;

import org.jspecify.annotations.Nullable;

import ch.scaille.javabeans.converters.IConverter;

/**
 *
 * @param <P> the property type
 */

public interface IChainBuilderFactory<P extends @Nullable Object> extends IChainBuilder<P> {

	<C extends @Nullable Object> IChainBuilderFactory<C> earlyBind(IConverter<P, C> conv);
	
}
