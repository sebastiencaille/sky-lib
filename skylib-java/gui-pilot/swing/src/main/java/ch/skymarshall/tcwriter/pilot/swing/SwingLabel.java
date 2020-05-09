package ch.skymarshall.tcwriter.pilot.swing;

import static ch.skymarshall.tcwriter.pilot.Polling.assertion;

import javax.swing.JLabel;

import org.junit.Assert;

public class SwingLabel extends AbstractSwingComponent<JLabel> {

	public SwingLabel(final GuiPilot pilot, final String name) {
		super(pilot, JLabel.class, name);
	}

	public void checkValue(final String value) {
		if (value == null) {
			return;
		}
		withReport(r -> "check text \'" + value + "\'")
				.waitReadSuccess(assertion(t -> Assert.assertEquals(value, t.getText())));
	}

}
