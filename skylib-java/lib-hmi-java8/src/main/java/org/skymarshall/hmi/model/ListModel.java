package org.skymarshall.hmi.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

import javax.swing.event.ListDataListener;

import org.skymarshall.hmi.model.views.IListView;

public abstract class ListModel<T> implements Iterable<T> {

	protected ListModelImpl<T> impl;

	public ListModel(final ListModelImpl<T> listModelImpl) {
		this.impl = listModelImpl;
		this.impl.setBase(this);
	}

	public abstract void setValues(final Collection<T> newData);

	public abstract void addValues(final Collection<T> newData);

	public abstract void clear();

	public abstract int insert(final T value);

	public abstract T remove(final T sample);

	public abstract T remove(final int row);

	public abstract T getEditedValue();

	public abstract IEdition<T> startEditingValue(final T value);

	public int getRowOf(final T value) {
		return impl.getRowOf(value);
	}

	public T getValueAt(final int row) {
		return impl.getValueAt(row);
	}

	public String getName() {
		return impl.getName();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	public Collection<T> values() {
		return impl.values();
	}

	/**
	 * Process the current edition and
	 */
	public abstract void stopEditingValue();

	/**
	 * Sets a new view on the list
	 *
	 * @param newView
	 */
	public void setView(final IListView<T> newView) {
		impl.setView(newView);
	}

	public T getElementAt(final int index) {
		return impl.getElementAt(index);
	}

	public int getSize() {
		return impl.getSize();
	}

	@Override
	public Iterator<T> iterator() {
		return impl.iterator();
	}

	public void addListener(final IListModelListener<T> listener) {
		impl.addListener(listener);
	}

	public void removeListener(final IListModelListener<T> listener) {
		impl.removeListener(listener);
	}

	public void addListDataListener(final ListDataListener listener) {
		impl.addListDataListener(listener);

	}

	public void removeListDataListener(final ListDataListener listener) {
		impl.addListDataListener(listener);
	}

	public T find(final T sample) {
		return impl.find(sample);
	}

	/**
	 * Finds an object in the model, and starts its edition if found
	 *
	 * @param sample
	 *            a sample of the object (must contains the values required to find
	 *            the object)
	 * @return an object if found, null if not
	 */
	public IEdition<T> findForEdition(final T sample) {
		return impl.findForEdition(sample);
	}

	/**
	 * Finds an object in the model, and starts its edition if found
	 *
	 * @param sample
	 *            a sample of the object (must contains the values required to find
	 *            the object)
	 * @return an object if found, null if not
	 */
	public void findAndEdit(final T sample, final Consumer<T> editor) {
		try (IEdition<T> edition = impl.findForEdition(sample)) {
			editor.accept(edition.edited());
		}
	}

	/**
	 * Finds an object in the model, or insert the sample if not found.
	 *
	 * @param sample
	 *            a sample of the object (must contains the values required to find
	 *            the object)
	 * @return an object if found, the sample if not found
	 */
	public T findOrCreate(final T sample) {
		return impl.findOrCreate(sample);
	}

	/**
	 * Finds an object in the model, starting it's edition, or insert the sample if
	 * not found.
	 *
	 * @param sample
	 *            a sample of the object (must contains the values required to find
	 *            the object)
	 * @return an object if found, the sample if not found
	 */
	public IEdition<T> findOrCreateForEdition(final T sample) {
		return impl.findOrCreateForEdition(sample);
	}

	/**
	 * Finds an object in the model, starting it's edition, or insert the sample if
	 * not found.
	 *
	 * @param sample
	 *            a sample of the object (must contains the values required to find
	 *            the object)
	 * @return an object if found, the sample if not found
	 */
	public void findOrCreateAndEdit(final T sample, final Consumer<T> editor) {
		try (IEdition<T> edition = impl.findOrCreateForEdition(sample)) {
			editor.accept(edition.edited());
		}
	}

	public void editValue(final T value, final Consumer<T> editor) {
		try (IEdition<T> edition = impl.startEditingValue(value)) {
			editor.accept(edition.edited());
		}
	}

	public void dispose() {
		impl.dispose();
	}

}
