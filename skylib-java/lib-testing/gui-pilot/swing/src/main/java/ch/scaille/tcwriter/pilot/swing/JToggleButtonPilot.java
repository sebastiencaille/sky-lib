package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.Factories.Reporting.checkingThat;

import javax.swing.JToggleButton;

public class JToggleButtonPilot extends AbstractSwingComponent<JToggleButtonPilot, JToggleButton> {

	public JToggleButtonPilot(final SwingPilot pilot, final String name) {
		super(pilot, JToggleButton.class, name);
	}

	public void assertSelected(final boolean expected) {
		polling(satisfies(c -> c.isSelected() == expected))
				.orFail(checkingThat("component is " + (expected ? "selected" : "not selected")));
	}

	public void setSelected(final boolean selected) {
		polling(applies(c -> c.setSelected(selected))).orFail((selected ? "selecting" : "deselecting"));
	}

}
