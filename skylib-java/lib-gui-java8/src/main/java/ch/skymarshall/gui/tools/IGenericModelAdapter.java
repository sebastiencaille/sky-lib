package ch.skymarshall.gui.tools;

import ch.skymarshall.gui.mvc.BindingChain.EndOfChain;

public interface IGenericModelAdapter {

	EndOfChain<?> apply(Class<?> editedClass, EndOfChain<?> chain);

}
