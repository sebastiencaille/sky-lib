package ch.scaille.gui.tools;

import ch.scaille.javabeans.BindingChain.EndOfChain;

public interface IGenericModelAdapter {

	EndOfChain<?> apply(Class<?> editedClass, EndOfChain<?> chain);

}
