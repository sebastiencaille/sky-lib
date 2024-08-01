package ch.scaille.gui.validation;

import ch.scaille.gui.tools.IGenericModelAdapter;
import ch.scaille.javabeans.BindingChain.EndOfChain;

/**
 * Model adapter that validates a class
 * @param <T>
 */
public class GenericEditorValidationAdapter<T> implements IGenericModelAdapter<T> {

	@Override
	public <U> EndOfChain<U> apply(final Class<T> editedClazz, final EndOfChain<U> chain) {
		return chain.bind(ValidationBinding.validator(editedClazz));
	}
}
