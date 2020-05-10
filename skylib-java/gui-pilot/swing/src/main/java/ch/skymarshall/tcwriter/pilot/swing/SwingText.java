package ch.skymarshall.tcwriter.pilot.swing;

import static ch.skymarshall.tcwriter.pilot.Polling.action;
import static ch.skymarshall.tcwriter.pilot.Polling.assertion;

import java.awt.event.KeyEvent;

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
				t.dispatchEvent(
						new KeyEvent(t, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, '\n'));
			}
		}));
	}

	public void checkTextValue(final String value) {
		if (value == null) {
			return;
		}
		withReport(r -> "check text \'" + value + "\'")
				.waitReadSuccess(assertion(t -> Assert.assertEquals(value, t.getText())));
	}

}
