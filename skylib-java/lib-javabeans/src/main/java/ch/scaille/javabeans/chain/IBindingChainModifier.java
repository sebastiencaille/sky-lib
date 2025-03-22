package ch.scaille.javabeans.chain;

import ch.scaille.javabeans.IBindingController;

public interface IBindingChainModifier extends IBindingController {

	void addLink(Link link);

	<T> void propagateComponentChange(Object component, T componentValue);

	boolean mustSendToProperty(IBindingChainModifier chain);

}
