package ch.scaille.gui.swing.bindings;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import ch.scaille.gui.mvc.ComponentBindingAdapter;
import ch.scaille.gui.swing.SwingExt;
import ch.scaille.javabeans.IComponentChangeSource;

public class JListContentBinding<T> extends ComponentBindingAdapter<List<T>> {

	private final JList<T> list;

	public JListContentBinding(final JList<T> component) {
		this.list = component;
	}

	@Override
	public void setComponentValue(final IComponentChangeSource source, final List<T> value) {
		final var newModel = new DefaultListModel<T>();
		value.forEach(newModel::addElement);
		list.setModel(newModel);
	}

	@Override
	public String toString() {
		return "Value of " + SwingExt.nameOf(list);
	}
}
