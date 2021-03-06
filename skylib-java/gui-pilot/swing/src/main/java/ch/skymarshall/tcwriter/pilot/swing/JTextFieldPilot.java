package ch.skymarshall.tcwriter.pilot.swing;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.junit.jupiter.api.Assertions;

public class JTextFieldPilot extends AbstractSwingComponent<JTextFieldPilot, JTextComponent> {

	public JTextFieldPilot(final SwingPilot pilot, final String name) {
		super(pilot, JTextComponent.class, name);
	}

	@Override
	protected boolean canEdit(final JTextComponent component) {
		return super.canEdit(component) && component.isEditable();
	}

	/**
	 * Select a value in a list, according to it's String representation
	 *
	 * @param componentName
	 * @param value
	 */
	public void setText(final String value) {
		if (value == null) {
			return;
		}
		wait(action(t -> {
			t.setText(value);
			if (t instanceof JTextField) {
				doPressReturn(t);
			}
		}).withReport(r -> "set text \'" + value + "\'"));
	}

	public void checkTextValue(final String value) {
		if (value == null) {
			return;
		}
		wait(assertion(t -> Assertions.assertEquals(value, t.getText())).withReport(r -> "check text \'" + value + "\'"));
	}

}
