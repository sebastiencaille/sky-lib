package ch.scaille.javabeans.chain;

import ch.scaille.javabeans.IChainBuilderFactory;
import ch.scaille.javabeans.converters.IConverter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class FirstEndOfChain<T extends @Nullable Object> extends EndOfChain<T> implements IChainBuilderFactory<T> {

	public FirstEndOfChain(IBindingChainModifier chain) {
		super(chain);
	}

	@Override
	public <C extends @Nullable Object> IChainBuilderFactory<C> earlyBind(IConverter<T, C> converter) {
		converter.initialize(chain.getProperty());
		chain.addLink(link(converter::convertPropertyValueToComponentValue,
                converter::convertComponentValueToPropertyValue));
		return new FirstEndOfChain<>(chain);
	} 
}
