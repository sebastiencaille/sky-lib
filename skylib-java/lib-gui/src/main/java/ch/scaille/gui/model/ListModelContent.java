package ch.scaille.gui.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.swing.AbstractListModel;
import javax.swing.event.EventListenerList;

import ch.scaille.gui.model.views.IListView;
import ch.scaille.gui.model.views.IListViewOwner;
import ch.scaille.gui.model.views.ListViews;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.util.helpers.StreamExt;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
@NullMarked
public class ListModelContent<T> extends AbstractListModel<T>
		implements ISourceModel<T>, Iterable<T>, ListModelRef<T> {

	@Serial
	private static final long serialVersionUID = 5327890361188939439L;

	private class Edition implements IEdition<T> {
		final T value;
		final int oldIndex;

		// Debug information
		private final StackTraceElement[] editionStack;

		/**
		 * True if object is not filtered out by the filter
		 */
		boolean accepted;

		public Edition(final T value, final int oldIndex) {
			this.value = value;
			this.oldIndex = oldIndex;
			accepted = viewProperty.getValue().accept(value);
			editionStack = Thread.currentThread().getStackTrace();
		}

		public boolean isAccepted() {
			return accepted;
		}

		@Override
		public String toString() {
			return "index=" + oldIndex;
		}

		public void updateAccepted() {
			accepted = viewProperty.getValue().accept(value);
		}

		@Override
		public T edited() {
			return value;
		}

		@Override
		public void close() {
			stopEditingValue();
		}

	}

	/**
	 * Marker
	 */
	private interface IChildModelListener<T> extends IListModelListener<T> {
//	just a marker
	}

	/**
	 * Listeners. Mostly used to handle list stacking and views
	 */
	private class ContentListeners implements IListViewOwner<T>, PropertyChangeListener, IChildModelListener<T>, Serializable {

		@Override
		public Comparator<T> parentComparator() {
			if (parent == null) {
				return null;
			}
            return parent.getView();
		}
		
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			viewProperty.getValue().attach(this);
			viewUpdated();
		}

		@Override
		public void viewUpdated() {
			ListModelContent.this.viewUpdated();
		}

		@Override
		public void valuesSet(final ListEvent<T> event) {
			setValues(event.getObjects());
		}

		@Override
		public void valuesCleared(final ListEvent<T> event) {
			clear();
		}

		@Override
		public void valuesAdded(final ListEvent<T> event) {
			if (event.getObjects().isEmpty()) {
				return;
			}
			if (data.size() / event.getObjects().size() < 2) {
				event.getObjects().forEach(ListModelContent.this::insert);
			} else {
				addValues(event.getObjects());
			}
		}

		@Override
		public void valuesRemoved(final ListEvent<T> event) {
			event.getObjects().forEach(ListModelContent.this::remove);// NOSONAR
		}

		@Override
		public void editionCancelled(final ListEvent<T> event) {
			objectEdition = null;
		}

		@SuppressWarnings("resource")
		@Override
		public void editionStarted(final ListEvent<T> event) {
			startEditingValue(event.getObject());
		}

		@Override
		public void editionStopping(final ListEvent<T> event) {
			// no op
		}

		@Override
		public void editionStopped(final ListEvent<T> event) {
			stopEditingValue();
		}

	}

	/**
	 * Current edition. If edition is in progress serialization may store a list
	 * with inconsistent order
	 */
	@Nullable
	private transient Edition objectEdition = null;

	private transient IPropertiesGroup changeSupport = PropertyChangeSupportController.mainGroup(this);

	private final EventListenerList listeners = new EventListenerList();

	private final ArrayList<T> data = new ArrayList<>();

	/**
	 * The current view
	 */
	private final ObjectProperty<IListView<T>> viewProperty = new ObjectProperty<>("View", changeSupport);

	@Nullable
	private final ListModelContent<T> parent;

	private final ContentListeners contentListeners = new ContentListeners();

	private String name = getClass().getSimpleName();

	private ListModel<T> base;

	public ListModelContent(final IListView<T> view) {
		this.parent = null;
		setView(view);
	}

	public ListModelContent(final ListModelContent<T> parent, final IListView<T> view) {
		this.parent = parent;
		attachToParent();
		setView(view);
	}

	public void setBase(final ListModel<T> base) {
		this.base = base;
	}

	@Override
	public ListModel<T> getListModel() {
		return base;
	}

	private void attachToParent() {
		parent.addListener(contentListeners);
		parent.viewProperty.addListener(contentListeners);
	}

	public void dispose() {
		if (parent != null) {
			parent.removeListener(contentListeners);
			parent.viewProperty.removeListener(contentListeners);
		}
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void addListener(final IListModelListener<T> listener) {
		listeners.add(IListModelListener.class, listener);
		if (listener instanceof IChildModelListener) {
			listeners.add(IChildModelListener.class, (IChildModelListener<T>) listener);
		}
	}

	public void removeListener(final IListModelListener<T> listener) {
		listeners.remove(IListModelListener.class, listener);
		if (listener instanceof IChildModelListener) {
			listeners.remove(IChildModelListener.class, (IChildModelListener<T>) listener);
		}
	}

	private <U extends EventListener> Stream<U> streamOf(Class<U> clazz) {
		return Arrays.stream(listeners.getListeners(clazz));
	}

	private void forEachListener(Consumer<IListModelListener<T>> action) {
		streamOf(IListModelListener.class).forEach(action::accept);
	}

	private void forEachListener(ListEvent<T> event, BiConsumer<IListModelListener<T>, ListEvent<T>> action) {
		streamOf(IListModelListener.class).forEach(l -> action.accept(l, event));
	}

	private void forEachChildListener(ListEvent<T> event, BiConsumer<IChildModelListener<T>, ListEvent<T>> action) {
		streamOf(IChildModelListener.class).forEach(l -> action.accept(l, event));
	}

	private ListEvent<T> listEvent(T object) {
		return new ListEvent<>(this, object);
	}

	private ListEvent<T> listEvent(List<T> objects) {
		return new ListEvent<>(this, objects);
	}

	private void fireMutating() {
		forEachListener(IListModelListener::mutates);
	}

	private void fireMutated() {
		forEachListener(IListModelListener::mutated);
	}

	protected void fireValuesSet(final List<T> set) {
		forEachListener(listEvent(set), IListModelListener::valuesSet);
	}

	protected void fireValuesCleared(final List<T> cleared) {
		forEachListener(listEvent(cleared), IListModelListener::valuesCleared);
	}

	protected void fireValuesAdded(final List<T> added) {
		forEachListener(listEvent(added), IListModelListener::valuesAdded);
	}

	protected void fireValueAdded(final T added) {
		forEachListener(listEvent(added), IListModelListener::valuesAdded);
	}

	protected void fireValueRemoved(final T removed) {
		forEachListener(listEvent(removed), IListModelListener::valuesRemoved);
	}

	private void fireEditionCancelled(final T edition) {
		forEachListener(listEvent(edition), IListModelListener::editionCancelled);
	}

	protected void fireEditionsStarted(final T edition) {
		forEachListener(listEvent(edition), IListModelListener::editionStarted);
	}

	protected void fireEditionStopping() {
		if (objectEdition == null) {
			return;
		}
		forEachListener(listEvent(objectEdition.value), IListModelListener::editionStopping);
	}

	protected void fireInnerEditionStopped() {
		forEachChildListener(listEvent(objectEdition.value), IChildModelListener::editionStopped);
	}

	protected void fireEditionStopped(final T value) {
		forEachListener(listEvent(value), IListModelListener::editionStopped);
	}

	public void fireViewUpdated() {
		viewProperty.forceChanged(this);
	}

	public IListView<T> getView() {
		return viewProperty.getValue();
	}

	private void viewUpdated() {
		fireMutating();
		rebuildModel();
		fireViewUpdated();
		fireMutated();
	}

	/**
	 * Sets a new view on the list
	 */
	public void setView(final IListView<T> newView) {
		if (viewProperty.getValue() != null) {
			viewProperty.getValue().detach(contentListeners);
		}
        viewProperty.setValue(this, Objects.requireNonNullElseGet(newView, ListViews::inherited));
		viewProperty.getValue().attach(contentListeners);
		viewUpdated();
	}

	private void rebuildModel() {
		checkNoEdition();
		final var newData = new ArrayList<T>();
		if (parent != null) {
			newData.addAll(parent.values());
		} else {
			newData.addAll(data);
		}
		setValues(newData);
	}

	@Override
	public T getElementAt(final int index) {
		return data.get(index);
	}

	@Override
	public int getSize() {
		return data.size();
	}

	@Override
	public @NonNull Iterator<T> iterator() {
		return new ArrayList<>(data).iterator();
	}

	@Override
	public void setValues(final Collection<T> newData) {
		checkNoEdition();
		fireMutating();
		clearModel();
		addToModel(newData);
		fireValuesSet(data);
	}

	@Override
	public void addValues(final Collection<T> newData) {
		checkNoEdition();
		fireMutating();
		final var addedData = addToModel(newData);
		fireValuesAdded(addedData);
	}

	protected List<T> addToModel(final Collection<T> newData) {
		final var oldSize = data.size();
		StreamExt.throwIfContainsNull(newData.stream());
		final var addedData = newData.stream().filter(viewProperty.getValue()::accept).toList();
		data.addAll(addedData);
		data.sort(viewProperty.getValue());
		fireContentsChanged(this, 0, oldSize - 1);
		if (!addedData.isEmpty()) {
			fireIntervalAdded(this, oldSize, data.size() - 1);
		}
		return addedData;
	}

	@Override
	public void clear() {
		checkNoEdition();
		fireMutating();
		final var removed = clearModel();
		fireValuesCleared(removed);
	}

	protected List<T> clearModel() {
		final var size = data.size();
		final var removed = new ArrayList<>(data);
		data.clear();
		fireIntervalRemoved(this, 0, size - 1);
		return removed;
	}

	private int computeInsertionPoint(final T value) {
		final var row = Collections.binarySearch(data, value, viewProperty.getValue());
		int index;
		if (row >= 0) {
			index = row;
		} else {
			index = -row - 1;
		}
		return index;
	}

	private T removeFromModel(final T value) {
		checkNoEdition();
		final var row = getRowOf(value);
		final T removed;
		if (row >= 0) {
			fireMutating();
			removed = data.remove(row);
			fireIntervalRemoved(this, row, row);
			fireValueRemoved(value);
		} else {
			removed = null;
		}
		return removed;
	}

	@Override
	public int insert(final T value) {
		checkNoEdition();
		if (value == null) {
			throw new IllegalArgumentException("Null value");
		}
		if (viewProperty.getValue().accept(value)) {
			fireMutating();
			final var row = computeInsertionPoint(value);
			data.add(row, value);
			fireIntervalAdded(this, row, row);
			fireValueAdded(value);
			return row;
		}
		return -1;
	}

	@Override
	public T remove(final T sample) {
		return removeFromModel(sample);
	}

	@Override
	public T remove(final int row) {
		final var value = getValueAt(row);
		removeFromModel(value);
		return value;
	}

	@Override
	public T getEditedValue() {
		if (objectEdition == null) {
			return null;
		}
		return objectEdition.value;
	}

	@Override
	public IEdition<T> startEditingValue(final T value) {
		if (value == null) {
			throw new IllegalArgumentException("value must not be null");
		}
		checkNoEdition();
		final var oldIndex = getRowOf(value);
		final var newEdition = new Edition(value, oldIndex);
		fireEditionsStarted(value);
		objectEdition = newEdition;
		return objectEdition;
	}

	protected void checkNoEdition() {
		if (objectEdition == null) {
			return;
		}
		final var builder = new StringBuilder();
		for (final var stack : objectEdition.editionStack) {
			builder.append(stack.toString()).append('\n');
		}
		throw new IllegalStateException(
				"Edition already in progress:" + objectEdition + ", editor stack=" + builder);
	}

	/**
	 * Process the current edition and
	 */
	@Override
	public void stopEditingValue() {
		if (objectEdition == null) {
			return;
		}
		final var value = objectEdition.value;
		try {
			fireEditionStopping();
			objectEdition.updateAccepted();
			if (!objectEdition.isAccepted() && objectEdition.oldIndex < 0) {
				// Edited object was not in the model and is still not in the model
				// Same assertion valid for children
				fireEditionCancelled(objectEdition.value);
			} else if (!objectEdition.isAccepted()) {
				// Edited object is removed from the model and from children
				fireEditionCancelled(objectEdition.value);
				fireMutating();
				data.remove(objectEdition.oldIndex);
				fireIntervalRemoved(this, objectEdition.oldIndex, objectEdition.oldIndex);
				fireValueRemoved(objectEdition.value);
			} else if (objectEdition.oldIndex < 0) {
				// Edited object is added to the model, and potentially to children
				fireEditionCancelled(objectEdition.value);
				fireMutating();
				final var newIndex = computeInsertionPoint(objectEdition.value);
				data.add(newIndex, objectEdition.value);
				fireIntervalAdded(this, newIndex, newIndex);
				fireValueAdded(objectEdition.value);
			} else {
				// Edited object may have moved. First remove the data, since it may
				// be at wrong location and this may confuse computeInsertionPoint
				var afterPrevious = objectEdition.oldIndex == 0 || viewProperty.getValue()
						.compare(data.get(objectEdition.oldIndex - 1), objectEdition.value) <= 0;
				var beforeNext = objectEdition.oldIndex == data.size() - 1 || viewProperty.getValue()
						.compare(objectEdition.value, data.get(objectEdition.oldIndex + 1)) <= 0;
				if (!afterPrevious || !beforeNext) {
					data.remove(objectEdition.oldIndex);
					final var newIndex = computeInsertionPoint(objectEdition.value);
					data.add(newIndex, objectEdition.value);
					fireIntervalRemoved(this, objectEdition.oldIndex, objectEdition.oldIndex);
					fireIntervalAdded(this, newIndex, newIndex);
				} else {
					// not moved
					fireContentsChanged(this, objectEdition.oldIndex, objectEdition.oldIndex);
				}
				fireInnerEditionStopped();
			}
		} finally {
			objectEdition = null;
		}
		fireEditionStopped(value);
	}

	public int getRowOf(final T value) {
		final var index = Collections.binarySearch(data, value, viewProperty.getValue());
		if (index < 0) {
			return index;
		}
		if (data.get(index).equals(value)) {
			return index;
		}
		var min = index - 1;
		while (min >= 0 && viewProperty.getValue().compare(value, data.get(min)) == 0) {
			if (data.get(min).equals(value)) {
				return min;
			}
			min--;
		}
		var max = index + 1;
		while (max < data.size() && viewProperty.getValue().compare(value, data.get(max)) == 0) {
			if (data.get(max).equals(value)) {
				return max;
			}
			max++;
		}
		return -index;
	}

	public T getValueAt(final int row) {
		return data.get(row);
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":[" + name + ", " + viewProperty + ']';
	}

	@Serial
	private void readObject(final java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		changeSupport = PropertyChangeSupportController.mainGroup(this);
	}

	@Override
	public T find(final T sample) {
		final var row = getRowOf(sample);
		if (row >= 0) {
			return getValueAt(row);
		}
		return null;
	}

	/**
	 * Finds an object in the model, and starts its edition if found
	 *
	 * @param sample a sample of the object (must contain the values required to
	 *               find the object)
	 * @return an object if found, null if not
	 */
	@Override
	public IEdition<T> findForEdition(final T sample) {
		final var found = find(sample);
		if (found != null) {
			return startEditingValue(found);
		}
		return null;
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
		var result = find(sample);
		if (result == null) {
			result = sample;
			insert(sample);
		}
		return result;
	}

	/**
	 * Finds an object in the model, starting its edition, or insert the sample if
	 * not found.
	 *
	 * @param sample a sample of the object (must contain the values required to
	 *               find the object)
	 * @return an object if found, the sample if not found
	 */
	@Override
	public IEdition<T> findOrCreateForEdition(final T sample) {
		final var found = findOrCreate(sample);
		return startEditingValue(found);
	}

	public List<T> values() {
		return new ArrayList<>(data);
	}

}
