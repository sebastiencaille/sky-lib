package ch.scaille.gui.swing.bindings;

import javax.swing.JSpinner;

import ch.scaille.gui.mvc.ComponentBindingAdapter;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.properties.AbstractProperty;

public class JSpinnerBinding<T extends Number> extends ComponentBindingAdapter<T> {

	private final JSpinner spinner;

	public JSpinnerBinding(final JSpinner component) {
		this.spinner = component;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<T> converter) {
		spinner.addChangeListener(e -> converter.setValueFromComponent(spinner, (T) spinner.getValue()));
	}

	@Override
	public void setComponentValue(final AbstractProperty source, final T value) {
		if (value != null) {
			spinner.setValue(value);
		} else {
			spinner.setValue(0);
		}
	}

	@Override
	public String toString() {
		return "Value of " + spinner;
	}
}
