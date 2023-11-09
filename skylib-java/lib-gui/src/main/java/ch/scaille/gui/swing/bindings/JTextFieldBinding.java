package ch.scaille.gui.swing.bindings;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextField;

import ch.scaille.gui.mvc.ComponentBindingAdapter;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.properties.AbstractProperty;

public class JTextFieldBinding extends ComponentBindingAdapter<String> {

	private final JTextField textField;
	private boolean withFocusLoss = true;

	public JTextFieldBinding(final JTextField component) {
		this.textField = component;
	}

	@Override
	public void addComponentValueChangeListener(final IComponentLink<String> converter) {

		final var original = textField.getActionMap().get(JTextField.notifyAction);
		textField.getActionMap().put(JTextField.notifyAction, new AbstractAction() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				converter.setValueFromComponent(textField, textField.getText());
				original.actionPerformed(event);
			}
		});
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				if (withFocusLoss) {
					converter.setValueFromComponent(textField, textField.getText());
				}
			}
		});
	}

	public JTextFieldBinding disableFocusLoss() {
		withFocusLoss = false;
		return this;
	}

	@Override
	public void setComponentValue(final AbstractProperty source, final String value) {
		if (value != null) {
			textField.setText(value);
		} else {
			textField.setText("");
		}
	}

	@Override
	public String toString() {
		return "Value of " + textField;
	}
}
