package ch.scaille.gui.model;

import java.util.Collection;

public interface IListModelDelegate<T> {

	void setValues(final Collection<T> newData);

	void addValues(final Collection<T> newData);

	void clear();

	int insert(final T value);

	T remove(final T sample);

	T remove(final int row);

	T getEditedValue();

	T find(T sample);

	/**
	 * Starts the edition of the sample
	 */
	IEdition<T> startEditingValue(final T sample);

	/**
	 * Stops the current object edition
	 */
	void stopEditingValue();

	T findOrCreate(final T sample);

	/**
	 * Finds an object in the model, and starts its edition if found
	 *
	 * @param sample a sample of the object (must contains the values required to
	 *               find the object)
	 * @return an object if found, null if not
	 */
	IEdition<T> findForEdition(T sample);

	/**
	 * Finds an object in the model, starting it's edition, or insert the sample if
	 * not found.
	 *
	 * @param sample a sample of the object (must contains the values required to
	 *               find the object)
	 * @return an object if found, the sample if not found
	 */
	IEdition<T> findOrCreateForEdition(final T sample);
}
