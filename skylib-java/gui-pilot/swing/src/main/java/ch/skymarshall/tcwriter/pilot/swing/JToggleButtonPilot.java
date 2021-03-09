package ch.skymarshall.tcwriter.pilot.swing;

import javax.swing.JToggleButton;

import org.junit.jupiter.api.Assertions;

public class JToggleButtonPilot extends AbstractSwingComponent<JToggleButtonPilot, JToggleButton> {

	public JToggleButtonPilot(final SwingPilot pilot, final String name) {
		super(pilot, JToggleButton.class, name);
	}

	public void waitSelected(final boolean selected) {
		wait(assertion(c -> Assertions.assertEquals(selected, c.isSelected()))
				.withReport(c -> "check " + (selected ? "selected" : "not selected")));
	}

	public void setSelected(final boolean selected) {
		wait(action(c -> c.setSelected(selected)).withReport(c -> "set " + (selected ? "selected" : "not selected")));
	}

}
