package ch.scaille.gui.model;

import ch.scaille.util.helpers.JavaExt;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

@NullMarked
public interface ISourceModel<T extends @Nullable Object> {

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
	 * Finds an object in the model and starts editing it if found
	 *
	 * @param sample a sample of the object (must contain the values required to
	 *               find the object)
	 */
	JavaExt.CloseableOptional<IEdition<T>> findForEdition(T sample);

	/**
	 * Finds an object in the model and start editing it, or insert the sample if
	 * not found.
	 *
	 * @param sample a sample of the object (must contain the values required to
	 *               find the object)
	 * @return an object if found, the sample if not found
	 */
	IEdition<T> findOrCreateForEdition(final T sample);
}
