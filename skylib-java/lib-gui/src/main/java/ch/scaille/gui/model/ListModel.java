package ch.scaille.gui.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.event.ListDataListener;

import ch.scaille.gui.model.views.IListView;
import ch.scaille.gui.model.views.StaticListView;
import ch.scaille.util.helpers.JavaExt;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * List Model with log(n) access.
 * <p>
 * Editions must start by calling startEditingValue(). Edition is
 * committed using stopEditingValue.<br>
 * Only one single edition can be made at a time, because the modification of
 * the edited entry may break the ordering of the list, making impossible to
 * compute the actual row of another edition with a log(n) complexity.
 * <p>
 * The sorting and filtering is done using an {@link IListView}. A default
 * implementation ({@link StaticListView ) is provided. Note
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
@NullMarked
public class ListModel<T extends @Nullable Object> implements ISourceModel<T>, Iterable<T>, Serializable {

	protected final ListModelContent<T> content;
	protected final transient ISourceModel<T> sourceCallbacks;
	@Nullable
	private String name = null;

	public ListModel(final IListView<T> view) {
		this.content = new ListModelContent<>(view);
		this.content.setBase(this);
		this.sourceCallbacks = content;
	}

	public ListModel(final ListModelContent<T> listModelImpl, ISourceModel<T> sourceCallbacks) {
		this.content = listModelImpl;
		this.content.setBase(this);
		this.sourceCallbacks = sourceCallbacks;
	}

	public ListModel<T> withName(String name) {
		this.name = name;
		return this;
	}

	public ListModel<T> child(IListView<T> view) {
		final var parent = this;
		final var newContent = new ListModelContent<>(content, view);
		newContent.addValues(parent.values());
		return new ListModel<>(newContent, parent); 
	}

	public int getRowOf(final T value) {
		return content.getRowOf(value);
	}

	@Override
	public T getValueAt(final int row) {
		return content.getValueAt(row);
	}

	public String getName() {
		return content.getName();
	}

	@Override
	public String toString() {
		if (name != null) {
			return name;
		}
		return getClass().getSimpleName();
	}

	public List<T> values() {
		return content.values();
	}

	/**
	 * Sets a new view on the list
	 */
	public void setView(final IListView<T> newView) {
		content.setView(newView);
	}

	public T getElementAt(final int index) {
		return content.getElementAt(index);
	}

	public int getSize() {
		return content.getSize();
	}

	@Override
	public Iterator<T> iterator() {
		return content.iterator();
	}

	public void addListener(final IListModelListener<T> listener) {
		content.addListener(listener);
	}

	public void removeListener(final IListModelListener<T> listener) {
		content.removeListener(listener);
	}

	public void addListDataListener(final ListDataListener listener) {
		content.addListDataListener(listener);

	}

	public void removeListDataListener(final ListDataListener listener) {
		content.addListDataListener(listener);
	}

	/**
	 * Finds an object in the model, and starts its edition if found
	 *
	 * @param sample a sample of the object (must contain the values required to
	 *               find the object)
	 */
	public void findAndEdit(final T sample, final Consumer<T> editor) {
		try (var edition = findForEdition(sample)) {
			edition.opt().ifPresent(edit -> editor.accept(edit.edited()));
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
		try (var edition = startEditingValue(sample)) {
			edition.opt().ifPresent(edit -> editor.accept(edit.edited()));
		}
	}

	@Override
	public void setValues(final Collection<T> newData) {
		sourceCallbacks.setValues(newData);
	}

	@Override
	public void addValues(final Collection<T> newData) {
		sourceCallbacks.addValues(newData);
	}

	@Override
	public void clear() {
		sourceCallbacks.clear();
	}

	@Override
	public int insert(final T value) {
		return sourceCallbacks.insert(value);
	}

	@Override
	public Optional<T> remove(final T sample) {
		return sourceCallbacks.remove(sample);
	}

	@Override
	public Optional<T> remove(final int row) {
		return sourceCallbacks.remove(row);
	}

	@Override
	public Optional<T> getEditedValue() {
		return sourceCallbacks.getEditedValue();
	}

	@Override
	public JavaExt.CloseableOptional<IEdition<T>> startEditingValue(final T value) {
		return sourceCallbacks.startEditingValue(value);
	}

	@Override
	public void stopEditingValue() {
		sourceCallbacks.stopEditingValue();
	}

	@Override
	public Optional<T> find(final T sample) {
		return sourceCallbacks.find(sample);
	}

	@Override
	public JavaExt.CloseableOptional<IEdition<T>> findForEdition(final T sample) {
		return sourceCallbacks.findForEdition(sample);
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
		return sourceCallbacks.findOrCreate(sample);
	}

	@Override
	public IEdition<T> findOrCreateForEdition(final T sample) {
		return sourceCallbacks.findOrCreateForEdition(sample);
	}

	public void dispose() {
		content.dispose();
	}

}
