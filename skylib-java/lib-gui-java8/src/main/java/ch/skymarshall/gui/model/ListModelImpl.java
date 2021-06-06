/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.gui.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.AbstractListModel;
import javax.swing.event.EventListenerList;

import ch.skymarshall.gui.model.views.IListView;
import ch.skymarshall.gui.model.views.IListViewOwner;
import ch.skymarshall.gui.model.views.ListViews;
import ch.skymarshall.gui.mvc.ControllerPropertyChangeSupport;
import ch.skymarshall.gui.mvc.IScopedSupport;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.util.helpers.StreamHelper;

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
 * implementation ({@link ch.skymarshall.gui.model.views.ListView) is provided.
 * Note that total ordering is mandatory to have a log(n) access. <p> The lists
 * can be stacked. If no ListView is defined for a list, the IListView of the
 * parent is used. <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> the type of the list's content. T must have an implementation of
 *            the Object.equals method. It is better if an element of the list
 *            can be uniquely identified using Object.equals.
 */
public class ListModelImpl<T> extends AbstractListModel<T>
		implements IListModelDelegate<T>, Iterable<T>, ListModelRef<T>, Serializable {

	private static final long serialVersionUID = 5327890361188939439L;

	private class Edition implements IEdition<T> {
		final T value;
		int oldIndex;

		// Debug information
		private transient StackTraceElement[] editionStack;

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
	 * Local listeners. Mostly used to handle list stacking
	 */
	private class LocalImpl implements IListViewOwner<T>, PropertyChangeListener, IChildModelListener<T>, Serializable {

		@Override
		public IListView<T> getParentView() {
			if (parent != null) {
				return parent.getView();
			}
			return null;
		}

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			viewProperty.getValue().attach(this);
			viewUpdated();
		}

		@Override
		public void viewUpdated() {
			ListModelImpl.this.viewUpdated();
		}

		@Override
		public void mutates() {
			// noop
		}

		@Override
		public void mutated() {
			// noop
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
				event.getObjects().forEach(ListModelImpl.this::insert);
			} else {
				addValues(event.getObjects());
			}
		}

		@Override
		public void valuesRemoved(final ListEvent<T> event) {
			event.getObjects().forEach(ListModelImpl.this::remove);// NOSONAR
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
	private transient Edition objectEdition = null;

	private transient IScopedSupport propertyChange = new ControllerPropertyChangeSupport(this).scoped(this);

	private final EventListenerList listeners = new EventListenerList();

	private final ArrayList<T> data = new ArrayList<>();

	/**
	 * The current view
	 */
	private final ObjectProperty<IListView<T>> viewProperty = new ObjectProperty<>("View", propertyChange);

	private final ListModelImpl<T> parent;

	private String name = getClass().getSimpleName();
	private final LocalImpl localImpl = new LocalImpl();

	private ListModel<T> base;

	public ListModelImpl(final IListView<T> view) {
		this.parent = null;
		if (view == null) {
			throw new IllegalArgumentException("View must not be null");
		}
		setView(view);
	}

	public ListModelImpl(final ListModelImpl<T> source, final IListView<T> view) {
		this.parent = source;
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
		parent.addListener(localImpl);
		parent.viewProperty.addListener(localImpl);
	}

	public void dispose() {
		if (parent != null) {
			parent.removeListener(localImpl);
			parent.viewProperty.removeListener(localImpl);
		}
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void addListener(final IListModelListener<T> listener) {
		listeners.add(IListModelListener.class, listener);
		if (listener instanceof IChildModelListener) { 
			listeners.add(IChildModelListener.class, IChildModelListener.class.cast(listener));
		}
	}

	public void removeListener(final IListModelListener<T> listener) {
		listeners.remove(IListModelListener.class, listener);
		if (listener instanceof IChildModelListener) {
			listeners.remove(IChildModelListener.class, IChildModelListener.class.cast(listener));
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
	 *
	 * @param newView
	 */
	public void setView(final IListView<T> newView) {
		if (viewProperty.getValue() != null) {
			viewProperty.getValue().detach(localImpl);
		}
		if (newView != null) {
			viewProperty.setValue(this, newView);
		} else {
			viewProperty.setValue(this, ListViews.<T>inherited());
		}
		viewProperty.getValue().attach(localImpl);
		viewUpdated();
	}

	private void rebuildModel() {
		checkNoEdition();
		final List<T> newData = new ArrayList<>();
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
	public Iterator<T> iterator() {
		return new ArrayList<>(data).iterator();
	}

	public void setValues(final Collection<T> newData) {
		checkNoEdition();
		fireMutating();
		clearModel();
		addToModel(newData);
		fireValuesSet(data);
	}

	public void addValues(final Collection<T> newData) {
		checkNoEdition();
		fireMutating();
		final List<T> addedData = addToModel(newData);
		fireValuesAdded(addedData);
	}

	protected List<T> addToModel(final Collection<T> newData) {
				final int oldSize = data.size();
		StreamHelper.throwIfContainsNull(newData.stream());
		final List<T> addedData = newData.stream().filter(viewProperty.getValue()::accept).collect(Collectors.toList());
		data.addAll(addedData);
		Collections.sort(data, viewProperty.getValue());
		fireContentsChanged(this, 0, oldSize - 1);
		if (!addedData.isEmpty()) {
			fireIntervalAdded(this, oldSize, data.size() - 1);
		}
		return addedData;
	}

	public void clear() {
		checkNoEdition();
		fireMutating();
		final List<T> removed = clearModel();
		fireValuesCleared(removed);
	}

	protected List<T> clearModel() {
		final int size = data.size();
		final List<T> removed = new ArrayList<>(data);
		data.clear();
		fireIntervalRemoved(this, 0, size - 1);
		return removed;
	}

	private int computeInsertionPoint(final T value) {
		final int row = Collections.binarySearch(data, value, viewProperty.getValue());
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
		final int row = getRowOf(value);
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

	public int insert(final T value) {
		checkNoEdition();
		if (value == null) {
			throw new IllegalArgumentException("Null value");
		}
		if (viewProperty.getValue().accept(value)) {
			fireMutating();
			final int row = computeInsertionPoint(value);
			data.add(row, value);
			fireIntervalAdded(this, row, row);
			fireValueAdded(value);
			return row;
		}
		return -1;
	}

	public T remove(final T sample) {
		return removeFromModel(sample);
	}

	public T remove(final int row) {
		final T value = getValueAt(row);
		removeFromModel(value);
		return value;
	}

	public T getEditedValue() {
		if (objectEdition == null) {
			return null;
		}
		return objectEdition.value;
	}

	public IEdition<T> startEditingValue(final T value) {
		if (value == null) {
			throw new IllegalArgumentException("value must not be null");
		}
		checkNoEdition();
		final int oldIndex = getRowOf(value);
		final Edition newEdition = new Edition(value, oldIndex);
		fireEditionsStarted(value);
		objectEdition = newEdition;
		return objectEdition;
	}

	protected void checkNoEdition() {
		if (objectEdition == null) {
			return;
		}
		final StringBuilder builder = new StringBuilder();
		for (final StackTraceElement stack : objectEdition.editionStack) {
			builder.append(stack.toString()).append('\n');
		}
		throw new IllegalStateException(
				"Edition already in progress:" + objectEdition + ", editor stack=" + builder.toString());
	}

	/**
	 * Process the current edition and
	 */
	public void stopEditingValue() {
		if (objectEdition == null) {
			return;
		}
		final T value = objectEdition.value;
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
				final int newIndex = computeInsertionPoint(objectEdition.value);
				data.add(newIndex, objectEdition.value);
				fireIntervalAdded(this, newIndex, newIndex);
				fireValueAdded(objectEdition.value);
			} else {
				// Edited object may have moved. First remove the data, since it may
				// be at wrong location and this may confuse computeInsertionPoint
				boolean afterPrevious = objectEdition.oldIndex == 0 || viewProperty.getValue()
						.compare(data.get(objectEdition.oldIndex - 1), objectEdition.value) <= 0;
				boolean beforeNext = objectEdition.oldIndex == data.size() - 1 || viewProperty.getValue()
						.compare(objectEdition.value, data.get(objectEdition.oldIndex + 1)) <= 0;
				if (!afterPrevious || !beforeNext) {
					data.remove(objectEdition.oldIndex);
					final int newIndex = computeInsertionPoint(objectEdition.value);
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
		final int index = Collections.binarySearch(data, value, viewProperty.getValue());
		if (index < 0) {
			return index;
		}
		if (data.get(index).equals(value)) {
			return index;
		}
		int min = index - 1;
		while (min >= 0 && viewProperty.getValue().compare(value, data.get(min)) == 0) {
			if (data.get(min).equals(value)) {
				return min;
			}
			min--;
		}
		int max = index + 1;
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
		return getClass().getSimpleName() + ":[" + name + ", " + viewProperty.toString() + ']';
	}

	private void readObject(final java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		propertyChange = new ControllerPropertyChangeSupport(this).scoped(this);
	}

	public T find(final T sample) {
		final int row = getRowOf(sample);
		if (row >= 0) {
			return getValueAt(row);
		}
		return null;
	}

	/**
	 * Finds an object in the model, and starts its edition if found
	 *
	 * @param sample a sample of the object (must contains the values required to
	 *               find the object)
	 * @return an object if found, null if not
	 */
	public IEdition<T> findForEdition(final T sample) {
		final T found = find(sample);
		if (found != null) {
			return startEditingValue(found);
		}
		return null;
	}

	/**
	 * Finds an object in the model, or insert the sample if not found.
	 *
	 * @param sample a sample of the object (must contains the values required to
	 *               find the object)
	 * @return an object if found, the sample if not found
	 */
	public T findOrCreate(final T sample) {
		T result = find(sample);
		if (result == null) {
			result = sample;
			insert(sample);
		}
		return result;
	}

	/**
	 * Finds an object in the model, starting it's edition, or insert the sample if
	 * not found.
	 *
	 * @param sample a sample of the object (must contains the values required to
	 *               find the object)
	 * @return an object if found, the sample if not found
	 */
	public IEdition<T> findOrCreateForEdition(final T sample) {
		final T found = findOrCreate(sample);
		return startEditingValue(found);
	}

	public List<T> values() {
		return new ArrayList<>(data);
	}

}
