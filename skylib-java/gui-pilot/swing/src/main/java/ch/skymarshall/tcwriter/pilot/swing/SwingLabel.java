package ch.skymarshall.tcwriter.pilot.swing;

import javax.swing.JLabel;

import org.junit.Assert;

public class SwingLabel extends AbstractSwingComponent<SwingLabel, JLabel> {

	public SwingLabel(final SwingGuiPilot pilot, final String name) {
		super(pilot, JLabel.class, name);
	}

	public void checkValue(final String value) {
		if (value == null) {
			return;
		}
		wait(assertion(t -> Assert.assertEquals(value, t.getText())).withReport(r -> "check text \'" + value + "\'"));
	}

}
