package ch.scaille.gui.validation;

import org.jspecify.annotations.Nullable;

import ch.scaille.gui.tools.IGenericModelAdapter;
import ch.scaille.javabeans.IChainBuilderFactory;

/**
 * Model adapter that validates a class
 * @param <T>
 */
public class GenericEditorValidationAdapter<T> implements IGenericModelAdapter<T> {

	@Override
	public <U extends @Nullable Object> IChainBuilderFactory<U> apply(final Class<T> editedClazz, final IChainBuilderFactory<U> chain) {
		return chain.earlyBind(ValidationBinding.validator(editedClazz));
	}
}
