package ch.scaille.gui.tools;

import ch.scaille.javabeans.IChainBuilder;

/**
 * Allows to add a binding in a binding chain
 */
public interface IGenericModelAdapter<T> {

	<U> IChainBuilder<U, Object> apply(Class<T> editedClass, IChainBuilder<U, Object> chain);

}
