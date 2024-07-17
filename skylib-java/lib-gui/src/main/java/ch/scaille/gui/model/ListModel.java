package ch.scaille.gui.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.event.ListDataListener;

import ch.scaille.gui.model.views.IListView;

/**
 * List Model with log(n) access.
 * <p>
 * List entry edition must start by calling startEditingValue(). Edition is
 * committed using stopEditingValue.<br>
 * Only one single edition can be made at a time, because the modification of
 * the edited entry may break the ordering of the list, making impossible to
 * compute the actual row of another edition with a log(n) complexity.
 * <p>
 * The sorting and filtering is done using an {@link IListView}. A default
 * implementation ({@link ch.scaille.gui.model.views.ListView) is provided. Note
 * that total ordering is mandatory to have a log(n) access. <p> The lists can
 * be stacked. If no ListView is defined for a list, the IListView of the parent
 * is used. <p>
 *
 * @author Sebastien Caille
 * <p>
 * @param <T> the type of the list's content. T must have an implementation of
 *            the Object.equals method. It is better if an element of the list
 *            can be uniquely identified using Object.equals.
 */
public class ListModel<T> implements IListModelDelegate<T>, Iterable<T>, Serializable {

	protected final ListModelImpl<T> impl;
	protected final IListModelDelegate<T> delegate;
	private String name = null;

	public ListModel(final IListView<T> view) {
		this.impl = new ListModelImpl<>(view);
		this.impl.setBase(this);
		this.delegate = impl;
	}

	public ListModel(final ListModelImpl<T> listModelImpl, IListModelDelegate<T> delegate) {
		this.impl = listModelImpl;
		this.impl.setBase(this);
		this.delegate = delegate;
	}

	public ListModel<T> withName(String name) {
		this.name = name;
		return this;
	}

	public ChildListModel<T> child(IListView<T> view) {
		return new ChildListModel<>(this, view);
	}

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
		if (name != null) {
			return name;
		}
		return getClass().getSimpleName();
	}

	public List<T> values() {
		return impl.values();
	}

	/**
	 * Sets a new view on the list
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

	/**
	 * Finds an object in the model, and starts its edition if found
	 *
	 * @param sample a sample of the object (must contain the values required to
	 *               find the object)
	 */
	public void findAndEdit(final T sample, final Consumer<T> editor) {
		try (var edition = findForEdition(sample)) {
			if (edition != null) {
				editor.accept(edition.edited());
			}
		}
	}

	/**
	 * Finds an object in the model, starting its edition, or insert the sample if
	 * not found.
	 *
	 * @param sample a sample of the object (must contain the values required to
	 *               find the object)
	 */
	public void findOrCreateAndEdit(final T sample, final Consumer<T> editor) {
		try (var edition = findOrCreateForEdition(sample)) {
			editor.accept(edition.edited());
		}
	}

	public void editValue(final T sample, final Consumer<T> editor) {
		try (var edition = impl.startEditingValue(sample)) {
			if (edition != null) {
				editor.accept(edition.edited());
			}
		}
	}

	@Override
	public void setValues(final Collection<T> newData) {
		delegate.setValues(newData);
	}

	@Override
	public void addValues(final Collection<T> newData) {
		delegate.addValues(newData);
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public int insert(final T value) {
		return delegate.insert(value);
	}

	@Override
	public T remove(final T sample) {
		return delegate.remove(sample);
	}

	@Override
	public T remove(final int row) {
		return delegate.remove(row);
	}

	@Override
	public T getEditedValue() {
		return delegate.getEditedValue();
	}

	@Override
	public IEdition<T> startEditingValue(final T value) {
		return delegate.startEditingValue(value);
	}

	@Override
	public void stopEditingValue() {
		delegate.stopEditingValue();
	}

	@Override
	public T find(final T sample) {
		return delegate.find(sample);
	}

	@Override
	public IEdition<T> findForEdition(final T sample) {
		return delegate.findForEdition(sample);
	}

	/**
	 * Finds an object in the model, or insert the sample if not found.
	 *
	 * @param sample a sample of the object (must contain the values required to
	 *               find the object)
	 * @return an object if found, the sample if not found
	 */
	@Override
	public T findOrCreate(final T sample) {
		return delegate.findOrCreate(sample);
	}

	@Override
	public IEdition<T> findOrCreateForEdition(final T sample) {
		return delegate.findOrCreateForEdition(sample);
	}

	public void dispose() {
		impl.dispose();
	}

}
