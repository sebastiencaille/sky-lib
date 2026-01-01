package ch.scaille.javabeans.chain;

import ch.scaille.javabeans.IBindingController;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * To change the structure of the binding chain
 */
@NullMarked
public interface IBindingChainModifier extends IBindingController {

	<P extends @Nullable Object, C extends @Nullable Object> void addLink(Link<P, C> link);

	<C extends @Nullable Object> void propagateComponentChange(Object component, C componentValue);

	boolean mustSendToProperty(IBindingChainModifier chain);

}
