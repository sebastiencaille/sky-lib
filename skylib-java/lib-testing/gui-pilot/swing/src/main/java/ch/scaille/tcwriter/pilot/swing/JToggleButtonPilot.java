package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.factories.Reporting.checkingThat;

import javax.swing.JToggleButton;

public class JToggleButtonPilot extends AbstractSwingComponentPilot<JToggleButtonPilot, JToggleButton> {

	public JToggleButtonPilot(final SwingPilot pilot, final String name) {
		super(pilot, JToggleButton.class, name);
	}

	public void assertSelected(final boolean expected) {
		polling().fail(checkingThat("component is " + (expected ? "selected" : "not selected")))
				.ifNot()
				.satisfied(c -> c.isSelected() == expected);
	}

	public void setSelected(final boolean selected) {
		polling().fail(selected ? "selecting" : "deselecting").ifNot().applied(c -> c.setSelected(selected));
	}

}
