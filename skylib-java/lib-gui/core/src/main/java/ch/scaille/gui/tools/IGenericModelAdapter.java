package ch.scaille.gui.tools;

import org.jspecify.annotations.Nullable;

import ch.scaille.javabeans.IChainBuilderFactory;

/**
 * Allows to add a binding in a binding chain
 */
public interface IGenericModelAdapter<T> {

	<U extends @Nullable Object> IChainBuilderFactory<U> apply(Class<T> editedClass, IChainBuilderFactory<U> chain);

}
