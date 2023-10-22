package ch.scaille.gui.validation;

import ch.scaille.gui.tools.IGenericModelAdapter;
import ch.scaille.javabeans.BindingChain.EndOfChain;

public class GenericEditorValidationAdapter implements IGenericModelAdapter {

	@Override
	public EndOfChain<?> apply(final Class<?> editedClazz, final EndOfChain<?> chain) {
		return chain.bind(ch.scaille.gui.validation.ValidationBinding.validator(editedClazz));
	}
}
