package ch.skymarshall.tcwriter.pilot.swing;

import javax.swing.JLabel;

import org.junit.jupiter.api.Assertions;

public class JLabelPilot extends AbstractSwingComponent<JLabelPilot, JLabel> {

	public JLabelPilot(final SwingPilot pilot, final String name) {
		super(pilot, JLabel.class, name);
	}

	public void checkValue(final String value) {
		if (value == null) {
			return;
		}
		wait(assertion(t -> Assertions.assertEquals(value, t.getText())).withReport(r -> "check text \'" + value + "\'"));
	}

}
