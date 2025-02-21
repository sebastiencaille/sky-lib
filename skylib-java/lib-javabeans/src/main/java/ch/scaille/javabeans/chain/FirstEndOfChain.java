package ch.scaille.javabeans.chain;

import ch.scaille.javabeans.IChainBuilder;
import ch.scaille.javabeans.IChainBuilderFactory;
import ch.scaille.javabeans.converters.IConverter;

public class FirstEndOfChain<T> extends EndOfChain<T, Object> implements IChainBuilderFactory<T> {

	private ContextGlue contextGlue = null;

	public FirstEndOfChain(BindingChain chain) {
		super(chain, null);
	}

	@Override
	public <K> IChainBuilder<T, K> withContext(K context) {
		contextGlue = new ContextGlue(context, chain.getProperty());
		return new EndOfChain<>(chain, context);
	}

	@Override
	public <C> IChainBuilderFactory<C> earlyBind(IConverter<T, C> converter) {
		chain.addLink(link(value -> converter.convertPropertyValueToComponentValue((T) value),
				value -> converter.convertComponentValueToPropertyValue((C)value)));
		return new FirstEndOfChain<>(chain);
	} 
}
