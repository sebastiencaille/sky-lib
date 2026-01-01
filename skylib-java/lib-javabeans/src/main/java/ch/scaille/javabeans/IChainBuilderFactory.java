package ch.scaille.javabeans;

import ch.scaille.javabeans.converters.IConverter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 *
 * @param <P> the property type
 */
@NullMarked
public interface IChainBuilderFactory<P extends @Nullable Object> extends IChainBuilder<P> {

	<C> IChainBuilderFactory<C> earlyBind(IConverter<P, C> conv);
	
}
