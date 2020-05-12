package ch.skymarshall.tcwriter.pilot.swing;

import static ch.skymarshall.tcwriter.pilot.Polling.action;
import static ch.skymarshall.tcwriter.pilot.Polling.assertion;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.junit.Assert;

public class SwingText extends AbstractSwingComponent<JTextComponent> {

	public SwingText(final SwingGuiPilot pilot, final String name) {
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
		withReport(r -> "set text \'" + value + "\'").waitEditSuccess(action(t -> {
			t.setText(value);
			if (t instanceof JTextField) {
				pressReturn(t);
			}
		}));
	}

	public void checkTextValue(final String value) {
		if (value == null) {
			return;
		}
		withReport(r -> "check text \'" + value + "\'")
				.waitStateSuccess(assertion(t -> Assert.assertEquals(value, t.getText())));
	}

}
