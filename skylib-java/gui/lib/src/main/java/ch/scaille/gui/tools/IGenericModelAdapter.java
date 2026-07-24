package ch.scaille.gui.tools;

import ch.scaille.javabeans.IChainBuilderFactory;
import org.jspecify.annotations.Nullable;

/**
 * Allows to add a binding in a binding chain
 */
public interface IGenericModelAdapter<T> {

	<U extends @Nullable Object> IChainBuilderFactory<U> apply(Class<T> editedClass, IChainBuilderFactory<U> chain);

}
