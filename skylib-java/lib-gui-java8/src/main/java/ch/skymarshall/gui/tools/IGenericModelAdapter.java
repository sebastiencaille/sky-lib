package ch.skymarshall.gui.tools;

import ch.skymarshall.gui.mvc.BindingChain.EndOfChain;

public interface IGenericModelAdapter {

	EndOfChain<Object> apply(Class<?> editedClass, EndOfChain<Object> chain);

}
