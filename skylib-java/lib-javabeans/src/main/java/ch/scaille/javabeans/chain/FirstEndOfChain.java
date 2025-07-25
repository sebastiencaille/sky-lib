package ch.scaille.javabeans.chain;

import ch.scaille.javabeans.IChainBuilderFactory;
import ch.scaille.javabeans.converters.IConverter;

public class FirstEndOfChain<T> extends EndOfChain<T> implements IChainBuilderFactory<T> {

	public FirstEndOfChain(IBindingChainModifier chain) {
		super(chain);
	}

	@Override
	public <C> IChainBuilderFactory<C> earlyBind(IConverter<T, C> converter) {
		converter.initialize(chain.getProperty());
		chain.addLink(link(value -> converter.convertPropertyValueToComponentValue((T) value),
				value -> converter.convertComponentValueToPropertyValue((C)value)));
		return new FirstEndOfChain<>(chain);
	} 
}
