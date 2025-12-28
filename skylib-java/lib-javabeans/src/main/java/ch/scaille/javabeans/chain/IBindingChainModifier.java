package ch.scaille.javabeans.chain;

import ch.scaille.javabeans.IBindingController;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * To change the structure of the binding chain
 */
@NullMarked
public interface IBindingChainModifier extends IBindingController {

	<P, C> void addLink(Link<P, C> link);

	<C> void propagateComponentChange(Object component, @Nullable C componentValue);

	boolean mustSendToProperty(IBindingChainModifier chain);

}
