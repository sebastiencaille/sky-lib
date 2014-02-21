/*******************************************************************************
 * Copyright (c) 2013 Sebastien Caille.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.event.EventListenerList;

import org.skymarshall.hmi.model.views.IListView;
import org.skymarshall.hmi.model.views.IListViewOwner;
import org.skymarshall.hmi.model.views.ListView;
import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;

/**
 * List Model with log(n) access.
 * <p>
 * List entry edition must start by calling startEditingValue(). Edition is
 * committed using stopEditingValue.<br>
 * Only one single edition can be made at a time, because the modification of
 * the edited entry may break the ordering of the list, making impossible to
 * compute the actual row of another edition with a log(n) complexity.
 * <p>
 * The sorting and filtering is done using an {@link IListView}. A default implementation  ({@link org.skymarshall.hmi.model.views.ListView) is provided.
 * Note that total ordering is mandatory to have a log(n) access.  
 * <p>
 * The lists can be stacked. If no ListView is defined for a list, the IListView of the
 * parent is used.
 * <p>
 * 
 * @author Sebastien Caille
 * 
 * @param <T>
 *            the type of the list's content. T must have an implementation of the Object.equals method. It is better if an element of the list can be uniquely identified using Object.equals. 
 */
public class ListModel<T> extends AbstractListModel<T> implements
        Iterable<T> {

    private static final long serialVersionUID = 5327890361188939439L;

    /**
     * An edition in progress
     * 
     * @author Sebastien Caille
     * 
     */
    private class Edition implements
            Serializable {
        final T value;
        int     oldIndex;
        /**
         * True if object is not filtered out by the filter
         */
        boolean accepted;

        public Edition(final T value, final int oldIndex) {
            this.value = value;
            this.oldIndex = oldIndex;
            accepted = viewProperty.getValue().accept(value);
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

    }

    private transient EventListenerList               listeners      = new EventListenerList();

    /**
     * Current edition
     */
    private Edition                                   objectEdition  = null;

    private transient ControllerPropertyChangeSupport propertyChange = new ControllerPropertyChangeSupport(this, false);

    private final List<T>                             data           = new ArrayList<T>();

    /**
     * The current view
     */
    private final ObjectProperty<IListView<T>>        viewProperty   = new ObjectProperty<IListView<T>>("View",
                                                                             propertyChange);

    private ListModel<T>                              parent;

    private String                                    name           = getClass().getSimpleName();

    /**
     * Local listeners. Mostly used to handle list stacking
     */
    private class LocalImpl implements
            IListViewOwner<T>,
            PropertyChangeListener,
            IListModelListener<T>,
            Serializable {

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
        public void mutates() {
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
                for (final T value : event.getObjects()) {
                    insert(value);
                }
            } else {
                addValues(event.getObjects());
            }
        }

        @Override
        public void valuesRemoved(final ListEvent<T> event) {
            for (final T value : event.getObjects()) {
                remove(value);
            }
        }

        @Override
        public void editionCancelled(final ListEvent<T> event) {
            objectEdition = null;
        }

        @Override
        public void editionsStarted(final ListEvent<T> event) {
            startEditingValue(event.getObject());
        }

        @Override
        public void editionsStopping(final ListEvent<T> event) {
            // no op
        }

        @Override
        public void editionsStopped(final ListEvent<T> event) {
            stopEditingValue();
        }

    }

    private final LocalImpl               localImpl = new LocalImpl();

    /**
     * Keep information about
     */
    private transient StackTraceElement[] editionStack;

    public ListModel(final IListView<T> view) {
        if (view == null) {
            throw new IllegalArgumentException("View must not be null");
        }
        setView(view);
    }

    public ListModel(final ListModel<T> source) {
        this.parent = source;
        attachToParent();
        setView(ListView.<T> inherited());
    }

    public ListModel(final ListModel<T> source, final IListView<T> view) {
        this.parent = source;
        attachToParent();
        setView(view);
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
    }

    public void removeListener(final IListModelListener<T> listener) {
        listeners.remove(IListModelListener.class, listener);
    }

    @SuppressWarnings("unchecked")
    private IListModelListener<T>[] listeners() {
        return listeners.getListeners(IListModelListener.class);
    }

    private void fireMutating() {
        for (final IListModelListener<T> listener : listeners()) {
            listener.mutates();
        }
    }

    protected void fireValuesSet(final List<T> set) {
        final ListEvent<T> event = new ListEvent<T>(this, set);
        for (final IListModelListener<T> listener : listeners()) {
            listener.valuesSet(event);
        }
    }

    protected void fireValuesCleared(final List<T> cleared) {
        final ListEvent<T> event = new ListEvent<T>(this, cleared);
        for (final IListModelListener<T> listener : listeners()) {
            listener.valuesCleared(event);
        }
    }

    protected void fireValuesAdded(final List<T> added) {
        final ListEvent<T> event = new ListEvent<T>(this, added);
        for (final IListModelListener<T> listener : listeners()) {
            listener.valuesAdded(event);
        }
    }

    protected void fireValueAdded(final T object) {
        final ListEvent<T> event = new ListEvent<T>(this, object);
        for (final IListModelListener<T> listener : listeners()) {
            listener.valuesAdded(event);
        }
    }

    protected void fireValueRemoved(final T object) {
        final ListEvent<T> event = new ListEvent<T>(this, object);
        for (final IListModelListener<T> listener : listeners()) {
            listener.valuesRemoved(event);
        }
    }

    private void fireEditionCancelled(final T value) {
        final ListEvent<T> event = new ListEvent<T>(this, value);
        for (final IListModelListener<T> listener : listeners()) {
            listener.editionCancelled(event);
        }
    }

    protected void fireEditionsStarted(final T value) {
        final ListEvent<T> event = new ListEvent<T>(this, value);
        for (final IListModelListener<T> listener : listeners()) {
            listener.editionsStarted(event);
        }
    }

    protected void fireEditionStopping() {
        if (objectEdition == null) {
            return;
        }
        final ListEvent<T> event = new ListEvent<T>(this, objectEdition.value);
        for (final IListModelListener<T> listener : listeners()) {
            listener.editionsStopping(event);
        }
    }

    protected void fireEditionStopped() {
        final ListEvent<T> event = new ListEvent<T>(this, objectEdition.value);
        for (final IListModelListener<T> listener : listeners()) {
            listener.editionsStopped(event);
        }
    }

    public void fireViewUpdated() {
        viewProperty.forceChanged(this);
    }

    public IListView<T> getView() {
        return viewProperty.getValue();
    }

    public void viewUpdated() {
        fireMutating();
        rebuildModel();
        fireViewUpdated();
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
            viewProperty.setValue(this, ListView.<T> inherited());
        }
        viewProperty.getValue().attach(localImpl);
        viewUpdated();
    }

    private void rebuildModel() {
        checkNoEdition();
        final List<T> newData = new ArrayList<T>();
        if (parent != null) {
            for (int i = 0; i < parent.getSize(); i++) {
                newData.add(parent.getValueAt(i));
            }
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
        return new ArrayList<T>(data).iterator();
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
        final List<T> addedData = new ArrayList<T>(newData.size());
        final int oldSize = data.size();
        for (final T value : newData) {
            if (viewProperty.getValue().accept(value)) {
                data.add(value);
                addedData.add(value);
            }
        }
        Collections.sort(data, viewProperty.getValue());
        if (addedData.size() > 0) {
            fireIntervalAdded(this, oldSize, data.size() - 1);
        }
        fireContentsChanged(this, 0, oldSize - 1);
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
        final List<T> removed = new ArrayList<T>(data);
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

    public void startEditingValue(final T value) {
        checkNoEdition();
        final int oldIndex = getRowOf(value);
        objectEdition = new Edition(value, oldIndex);
        editionStack = Thread.currentThread().getStackTrace();
        fireEditionsStarted(value);
    }

    protected void checkNoEdition() {
        if (objectEdition == null) {
            return;
        }
        final StringBuilder builder = new StringBuilder();
        for (final StackTraceElement stack : editionStack) {
            builder.append(stack.toString());
            builder.append('\n');
        }
        throw new IllegalStateException("Edition already in progress:" + objectEdition + ", editor stack="
                + builder.toString());
    }

    /**
     * Process the current edition and
     */
    public void stopEditingValue() {
        // if (verbose()) {
        // log("edition stopping");
        // }
        fireEditionStopping();
        if (objectEdition == null) {
            return;
        }

        objectEdition.updateAccepted();
        if (!objectEdition.isAccepted() && objectEdition.oldIndex < 0) {
            // Edited object was not in the model and is still not in the model
            // Same assertion valid for child models
            fireEditionCancelled(objectEdition.value);
        } else if (!objectEdition.isAccepted()) {
            // Edited object is removed from the model, and potentially from
            // child models
            fireEditionCancelled(objectEdition.value);
            fireMutating();
            data.remove(objectEdition.oldIndex);
            fireIntervalRemoved(this, objectEdition.oldIndex, objectEdition.oldIndex);
            fireValueRemoved(objectEdition.value);
        } else if (objectEdition.oldIndex < 0) {
            // Edited object is added to the model, and potentially to child
            // models
            fireEditionCancelled(objectEdition.value);
            fireMutating();
            final int newIndex = computeInsertionPoint(objectEdition.value);
            data.add(newIndex, objectEdition.value);
            fireIntervalAdded(this, newIndex, newIndex);
            fireValueAdded(objectEdition.value);
        } else {
            // Edited object may have moved. First remove the data, since it may
            // be at wrong location and this may confuse computeInsertionPoint
            data.remove(objectEdition.oldIndex);
            final int newIndex = computeInsertionPoint(objectEdition.value);
            data.add(newIndex, objectEdition.value);
            if (objectEdition.oldIndex == newIndex) {
                fireContentsChanged(this, newIndex, newIndex);
            } else {
                fireIntervalRemoved(this, objectEdition.oldIndex, objectEdition.oldIndex);
                fireIntervalAdded(this, newIndex, newIndex);
            }
            fireEditionStopped();
        }
        objectEdition = null;
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
        listeners = new EventListenerList();
        propertyChange = new ControllerPropertyChangeSupport(this);
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
     * @param sample
     *            a sample of the object (must contains the values required to
     *            find the object)
     * @return an object if found, null if not
     */
    public T findForEdition(final T sample) {
        final T found = find(sample);
        if (found != null) {
            startEditingValue(found);
        }
        return found;
    }

    /**
     * Finds an object in the model, or insert the sample if not found.
     * 
     * @param sample
     *            a sample of the object (must contains the values required to
     *            find the object)
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
     * Finds an object in the model, starting it's edition, or insert the sample
     * if not found.
     * 
     * @param sample
     *            a sample of the object (must contains the values required to
     *            find the object)
     * @return an object if found, the sample if not found
     */
    public T findOrCreateForEdition(final T sample) {
        final T found = findOrCreate(sample);
        startEditingValue(found);
        return found;
    }

    public PropertyChangeListener getViewUpdatedListener() {
        return localImpl;
    }

}
