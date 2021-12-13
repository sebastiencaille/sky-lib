package ch.skymarshall.tcwriter.pilot.swing;

import javax.swing.JToggleButton;

public class JToggleButtonPilot extends AbstractSwingComponent<JToggleButtonPilot, JToggleButton> {

	public JToggleButtonPilot(final SwingPilot pilot, final String name) {
		super(pilot, JToggleButton.class, name);
	}

	public void waitSelected(final boolean expected) {
		wait(assertEquals("check " + (expected ? "selected" : "not selected"), expected, JToggleButton::isSelected));
	}

	public void setSelected(final boolean selected) {
		wait(action(c -> c.setSelected(selected)).withReportText((selected ? "select" : "deselect")));
	}

}
