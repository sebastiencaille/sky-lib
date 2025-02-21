package ch.scaille.gui.validation;

import ch.scaille.gui.tools.IGenericModelAdapter;
import ch.scaille.javabeans.IChainBuilder;

/**
 * Model adapter that validates a class
 * @param <T>
 */
public class GenericEditorValidationAdapter<T> implements IGenericModelAdapter<T> {

	@Override
	public <U> IChainBuilder<U, Object> apply(final Class<T> editedClazz, final IChainBuilder<U, Object> chain) {
		return chain.bind(ValidationBinding.validator(editedClazz));
	}
}
