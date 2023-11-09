package ch.scaille.gui.swing.bindings;

import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import ch.scaille.gui.mvc.ComponentBindingAdapter;
import ch.scaille.gui.swing.factories.SwingBindings;
import ch.scaille.javabeans.properties.AbstractProperty;

public class JComboBoxContentBinding<T, U extends Collection<T>> extends ComponentBindingAdapter<U> {

	private final JComboBox<T> box;

	public JComboBoxContentBinding(final JComboBox<T> component) {
		this.box = component;
	}

	@Override
	public void setComponentValue(final AbstractProperty source, final U value) {
		final var newModel = new DefaultComboBoxModel<T>();
		value.forEach(newModel::addElement);
		box.setModel(newModel);
	}

	@Override
	public String toString() {
		return "Value of " + SwingBindings.nameOf(box);
	}
}
