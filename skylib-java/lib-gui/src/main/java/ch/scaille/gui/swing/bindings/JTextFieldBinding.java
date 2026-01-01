package ch.scaille.gui.swing.bindings;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.JTextField;

import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class JTextFieldBinding implements IComponentBinding<String> {

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
	public void setComponentValue(final IComponentChangeSource source, @Nullable final String value) {
        textField.setText(Objects.requireNonNullElse(value, ""));
	}

	@Override
	public String toString() {
		return "Value of " + textField;
	}
}
