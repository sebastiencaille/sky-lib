package ch.scaille.gui.model;

import ch.scaille.util.helpers.JavaExt;

import java.util.Collection;
import java.util.Optional;

public interface ISourceModel<T> {

	void setValues(final Collection<T> newData);

	void addValues(final Collection<T> newData);

	void clear();

	int insert(final T value);

	Optional<T> remove(final T sample);

	Optional<T> remove(final int row);

	Optional<T> getEditedValue();


	Optional<T> find(T sample);

	/**
	 * Starts the edition of the sample
	 */
	JavaExt.CloseableOptional<IEdition<T>> startEditingValue(final T sample);

	/**
	 * Stops the current object edition
	 */
	void stopEditingValue();

	T findOrCreate(final T sample);

	/**
	 * Finds an object in the model, and starts its edition if found
	 *
	 * @param sample a sample of the object (must contain the values required to
	 *               find the object)
	 * @return an object if found, null if not
	 */
	JavaExt.CloseableOptional<IEdition<T>> findForEdition(T sample);

	/**
	 * Finds an object in the model, starting its edition, or insert the sample if
	 * not found.
	 *
	 * @param sample a sample of the object (must contain the values required to
	 *               find the object)
	 * @return an object if found, the sample if not found
	 */
	IEdition<T> findOrCreateForEdition(final T sample);
}
