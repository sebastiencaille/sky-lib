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

import java.util.Collection;

import ch.skymarshall.gui.model.views.IListView;

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
public class RootListModel<T> extends ListModel<T> {

	public RootListModel(final IListView<T> view) {
		super(new ListModelImpl<>(view));
	}

	@Override
	public void setValues(final Collection<T> newData) {
		impl.setValues(newData);
	}

	@Override
	public void addValues(final Collection<T> newData) {
		impl.addValues(newData);
	}

	@Override
	public void clear() {
		impl.clear();
	}

	@Override
	public int insert(final T value) {
		return impl.insert(value);
	}

	@Override
	public T remove(final T sample) {
		return impl.remove(sample);
	}

	@Override
	public T remove(final int row) {
		return impl.remove(row);
	}

	@Override
	public T getEditedValue() {
		return impl.getEditedValue();
	}

	@Override
	public IEdition<T> startEditingValue(final T value) {
		return impl.startEditingValue(value);
	}

	@Override
	public void stopEditingValue() {
		impl.stopEditingValue();
	}

}
