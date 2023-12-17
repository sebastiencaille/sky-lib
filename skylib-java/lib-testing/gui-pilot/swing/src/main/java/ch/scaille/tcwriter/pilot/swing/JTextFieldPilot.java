package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.Factories.Reporting.checkingValue;
import static ch.scaille.tcwriter.pilot.Factories.Reporting.settingValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

@SuppressWarnings("java:S5960")
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
		waitOn(action(t -> {
			t.setText(value);
			if (t instanceof JTextField) {
				SwingHelper.doPressReturn(t);
			}
		}).withReportText(settingValue(value)));
	}

	public void checkTextValue(final String expected) {
		if (expected == null) {
			return;
		}
		waitOn(assertion(pc -> assertEquals(expected, pc.component.getText(), pc.description))
				.withReportText(checkingValue(expected)));
	}

}
