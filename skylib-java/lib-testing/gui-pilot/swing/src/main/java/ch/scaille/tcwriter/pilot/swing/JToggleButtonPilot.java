package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.factories.Reporting.checkingThat;

import javax.swing.JToggleButton;

public class JToggleButtonPilot extends AbstractSwingComponentPilot<JToggleButtonPilot, JToggleButton> {

	public JToggleButtonPilot(final SwingPilot pilot, final String name) {
		super(pilot, JToggleButton.class, name);
	}

	public void assertSelected(final boolean expected) {
		polling().trySatisfy(c -> c.isSelected() == expected)
				.orFail(checkingThat("component is " + (expected ? "selected" : "not selected")));
	}

	public void setSelected(final boolean selected) {
		polling().tryApply(c -> c.setSelected(selected)).orFail((selected ? "selecting" : "deselecting"));
	}

}
