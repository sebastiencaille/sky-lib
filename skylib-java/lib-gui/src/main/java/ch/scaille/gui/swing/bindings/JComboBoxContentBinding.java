package ch.scaille.gui.swing.bindings;

import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import ch.scaille.gui.swing.SwingExt;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentChangeSource;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class JComboBoxContentBinding<T, U extends @NonNull Collection<T>> implements IComponentBinding<U> {

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
