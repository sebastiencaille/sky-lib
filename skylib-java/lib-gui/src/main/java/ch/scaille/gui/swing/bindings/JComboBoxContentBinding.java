package ch.scaille.gui.swing.bindings;

import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import ch.scaille.gui.mvc.ComponentBindingAdapter;
import ch.scaille.gui.swing.SwingExt;
import ch.scaille.javabeans.IComponentChangeSource;

public class JComboBoxContentBinding<T, U extends Collection<T>> extends ComponentBindingAdapter<U> {

	private final JComboBox<T> box;

	public JComboBoxContentBinding(final JComboBox<T> component) {
		this.box = component;
	}

	@Override
	public void setComponentValue(final IComponentChangeSource source, final U value) {
		final var newModel = new DefaultComboBoxModel<T>();
		value.forEach(newModel::addElement);
		box.setModel(newModel);
	}

	@Override
	public String toString() {
		return "Value of " + SwingExt.nameOf(box);
	}
}
