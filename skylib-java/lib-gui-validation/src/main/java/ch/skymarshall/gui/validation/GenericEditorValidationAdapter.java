package ch.skymarshall.gui.validation;

import ch.skymarshall.gui.mvc.BindingChain.EndOfChain;
import ch.skymarshall.gui.tools.IGenericModelAdapter;

public class GenericEditorValidationAdapter implements IGenericModelAdapter {

	@Override
	public EndOfChain<?> apply(final Class<?> editedClazz, final EndOfChain<?> chain) {
		return chain.bind(ch.skymarshall.gui.validation.ValidationBinding.validator(editedClazz));
	}
}
