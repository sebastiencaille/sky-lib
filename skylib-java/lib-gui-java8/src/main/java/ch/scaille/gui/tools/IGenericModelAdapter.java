package ch.scaille.gui.tools;

import ch.scaille.gui.mvc.BindingChain.EndOfChain;

public interface IGenericModelAdapter {

	EndOfChain<?> apply(Class<?> editedClass, EndOfChain<?> chain);

}
