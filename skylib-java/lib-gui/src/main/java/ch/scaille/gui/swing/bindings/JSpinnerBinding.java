package ch.scaille.gui.swing.bindings;

import javax.swing.JSpinner;

import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class JSpinnerBinding<T extends Number> implements IComponentBinding<T> {

	private final JSpinner spinner;

	public JSpinnerBinding(final JSpinner component) {
		this.spinner = component;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<T> converter) {
		spinner.addChangeListener(e -> converter.setValueFromComponent(spinner, (T) spinner.getValue()));
	}

	@Override
	public void setComponentValue(final IComponentChangeSource source, final T value) {
        spinner.setValue(value);
	}

	@Override
	public String toString() {
		return "Value of " + spinner;
	}
}
