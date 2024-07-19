package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.factories.Reporting.checkingValue;
import static ch.scaille.tcwriter.pilot.factories.Reporting.settingValue;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.junit.jupiter.api.Assertions;

import ch.scaille.tcwriter.pilot.PollingContext;

@SuppressWarnings("java:S5960")
public class JTextFieldPilot extends AbstractSwingComponentPilot<JTextComponent> {

	public JTextFieldPilot(final SwingPilot pilot, final String name) {
		super(pilot, JTextComponent.class, name);
	}

	@Override
	protected boolean canEdit(final PollingContext<JTextComponent> ctxt) {
		return super.canEdit(ctxt) && ctxt.getComponent().isEditable();
	}

	/**
	 * Select a value in a list, according to its String representation
	 */
	public void setText(final String value) {
		if (value == null) {
			return;
		}
		polling().fail(settingValue(value)).ifNot().applied(t -> {
			t.setText(value);
			if (t instanceof JTextField) {
				SwingHelper.doPressReturn(t);
			}
		});
	}

	public void assertTextValue(final String expected) {
		if (expected == null) {
			return;
		}
		polling().fail(checkingValue(expected))
				.ifNot()
				.asserted(pc -> Assertions.assertEquals(expected, pc.getComponent().getText(), pc.getDescription()));
	}

}
