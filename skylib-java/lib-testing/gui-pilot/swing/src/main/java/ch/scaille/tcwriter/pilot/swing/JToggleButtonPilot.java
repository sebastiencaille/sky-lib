package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.Factories.checkingThat;

import javax.swing.JToggleButton;

public class JToggleButtonPilot extends AbstractSwingComponent<JToggleButtonPilot, JToggleButton> {

	public JToggleButtonPilot(final SwingPilot pilot, final String name) {
		super(pilot, JToggleButton.class, name);
	}

	public void waitSelected(final boolean expected) {
		wait(satisfies(c -> c.isSelected() == expected)
				.withReportText(checkingThat("component is " + (expected ? "selected" : "not selected"))));
	}

	public void setSelected(final boolean selected) {
		wait(action(c -> c.setSelected(selected)).withReportText((selected ? "selecting" : "deselecting")));
	}

}
