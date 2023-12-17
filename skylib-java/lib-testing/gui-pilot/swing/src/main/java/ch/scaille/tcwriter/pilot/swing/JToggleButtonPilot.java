package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.Factories.Reporting.checkingThat;

import javax.swing.JToggleButton;

public class JToggleButtonPilot extends AbstractSwingComponent<JToggleButtonPilot, JToggleButton> {

	public JToggleButtonPilot(final SwingPilot pilot, final String name) {
		super(pilot, JToggleButton.class, name);
	}

	public void waitSelected(final boolean expected) {
		waitOn(satisfies(c -> c.isSelected() == expected)
				.withReportText(checkingThat("component is " + (expected ? "selected" : "not selected"))));
	}

	public void setSelected(final boolean selected) {
		waitOn(action(c -> c.setSelected(selected)).withReportText((selected ? "selecting" : "deselecting")));
	}

}
