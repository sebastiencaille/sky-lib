package ch.scaille.gui.swing.bindings;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import ch.scaille.gui.mvc.ComponentBindingAdapter;
import ch.scaille.gui.swing.factories.SwingBindings;
import ch.scaille.javabeans.properties.AbstractProperty;

public class JListContentBinding<T> extends ComponentBindingAdapter<List<T>> {

	private final JList<T> list;

	public JListContentBinding(final JList<T> component) {
		this.list = component;
	}

	@Override
	public void setComponentValue(final AbstractProperty source, final List<T> value) {
		final var newModel = new DefaultListModel<T>();
		value.forEach(newModel::addElement);
		list.setModel(newModel);
	}

	@Override
	public String toString() {
		return "Value of " + SwingBindings.nameOf(list);
	}
}
