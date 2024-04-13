package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.factories.Reporting.checkingValue;
import static ch.scaille.tcwriter.pilot.factories.Reporting.settingValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import ch.scaille.tcwriter.pilot.PollingContext;

@SuppressWarnings("java:S5960")
public class JTextFieldPilot extends AbstractSwingComponentPilot<JTextFieldPilot, JTextComponent> {

	public JTextFieldPilot(final SwingPilot pilot, final String name) {
		super(pilot, JTextComponent.class, name);
	}

	@Override
	protected boolean canEdit(final PollingContext<JTextComponent> ctxt) {
		return super.canEdit(ctxt) && ctxt.getComponent().isEditable();
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
		polling().apply(t -> {
			t.setText(value);
			if (t instanceof JTextField) {
				SwingHelper.doPressReturn(t);
			}
		}).orFail(settingValue(value));
	}

	public void assertTextValue(final String expected) {
		if (expected == null) {
			return;
		}
		polling().asserts(pc -> assertEquals(expected, pc.getComponent().getText(), pc.getDescription()))
				.orFail(checkingValue(expected));
	}

}
