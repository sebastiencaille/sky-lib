package ch.scaille.gui.tools;

import ch.scaille.javabeans.BindingChain.EndOfChain;

/**
 * Allows to add a binding in a bionding chain
 */
public interface IGenericModelAdapter {

	EndOfChain<?> apply(Class<?> editedClass, EndOfChain<?> chain);

}
