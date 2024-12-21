package ch.scaille.gui.swing.bindings;

import java.util.Objects;

import javax.swing.JSpinner;

import ch.scaille.gui.mvc.ComponentBindingAdapter;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;

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
	public void setComponentValue(final IComponentChangeSource source, final T value) {
        spinner.setValue(Objects.requireNonNullElse(value, 0));
	}

	@Override
	public String toString() {
		return "Value of " + spinner;
	}
}
