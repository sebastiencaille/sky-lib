package ch.scaille.gui.tools;

import ch.scaille.javabeans.IChainBuilderFactory;

/**
 * Allows to add a binding in a binding chain
 */
public interface IGenericModelAdapter<T> {

	<U> IChainBuilderFactory<U> apply(Class<T> editedClass, IChainBuilderFactory<U> chain);

}
