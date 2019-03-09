package org.skymarshall.hmi.model;

import java.util.Collection;

import org.skymarshall.hmi.model.views.IListView;

public class ChildListModel<T> extends ListModel<T> {

	private final ListModel<T> parent;

	public ChildListModel(final ListModel<T> parent, final IListView<T> view) {
		super(new ListModelImpl<>(parent.impl, view));
		this.parent = parent;
		reload();
	}

	public ChildListModel(final ListModel<T> parent) {
		super(new ListModelImpl<>(parent.impl));
		this.parent = parent;
	}

	public void reload() {
		impl.clear();
		impl.addValues(parent.values());
	}

	@Override
	public void setValues(final Collection<T> newData) {
		parent.setValues(newData);
	}

	@Override
	public void addValues(final Collection<T> newData) {
		parent.addValues(newData);
	}

	@Override
	public void clear() {
		parent.clear();
	}

	@Override
	public int insert(final T value) {
		return parent.insert(value);
	}

	@Override
	public T remove(final T sample) {
		return parent.remove(impl.find(sample));
	}

	@Override
	public T remove(final int row) {
		return parent.remove(impl.getElementAt(row));
	}

	@Override
	public T getEditedValue() {
		return parent.getEditedValue();
	}

	@Override
	public IEdition<T> startEditingValue(final T value) {
		return parent.startEditingValue(value);
	}

	@Override
	public void stopEditingValue() {
		parent.stopEditingValue();
	}
}
