package ch.scaille.gui.tools;

import ch.scaille.javabeans.BindingChain.EndOfChain;

/**
 * Allows to add a binding in a binding chain
 */
public interface IGenericModelAdapter<T> {

	<U> EndOfChain<U> apply(Class<T> editedClass, EndOfChain<U> chain);

}
