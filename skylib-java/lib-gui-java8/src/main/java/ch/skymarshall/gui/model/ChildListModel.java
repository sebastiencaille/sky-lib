package ch.skymarshall.gui.model;

import ch.skymarshall.gui.model.views.IListView;

public class ChildListModel<T> extends ListModel<T> {

	private final ListModel<T> parent;

	public ChildListModel(final ListModel<T> parent, final IListView<T> view) {
		super(new ListModelImpl<>(parent.impl, view), parent);
		this.parent = parent;
		reload();
	}

	public void reload() {
		impl.clear();
		impl.addValues(parent.values());
	}

}
