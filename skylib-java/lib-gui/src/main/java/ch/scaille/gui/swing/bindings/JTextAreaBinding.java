package ch.scaille.gui.swing.bindings;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Objects;

import javax.swing.JTextArea;

import ch.scaille.gui.mvc.ComponentBindingAdapter;
import ch.scaille.gui.swing.factories.SwingBindings;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.properties.AbstractProperty;

public class JTextAreaBinding extends ComponentBindingAdapter<String> {

	private final JTextArea textArea;
	private final boolean readOnly;

	public JTextAreaBinding(final JTextArea component) {
		this.textArea = component;
		readOnly = false;
	}

	public JTextAreaBinding(final JTextArea component, final boolean readOnly) {
		this.textArea = component;
		this.readOnly = readOnly;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<String> converter) {
		if (readOnly) {
			return;
		}
		textArea.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				converter.setValueFromComponent(textArea, textArea.getText());
			}
		});
	}

	@Override
	public void setComponentValue(final AbstractProperty source, final String value) {
        textArea.setText(Objects.requireNonNullElse(value, ""));
	}

	@Override
	public String toString() {
		return "Value of " + SwingBindings.nameOf(textArea);
	}

}
