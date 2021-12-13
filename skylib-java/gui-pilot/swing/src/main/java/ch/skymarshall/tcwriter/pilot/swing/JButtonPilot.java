package ch.skymarshall.tcwriter.pilot.swing;

import javax.swing.JButton;

public class JButtonPilot extends AbstractSwingComponent<JButtonPilot, JButton> {

	public JButtonPilot(final SwingPilot pilot, final String name) {
		super(pilot, JButton.class, name);
	}

	public void click() {
		wait(action(JButton::doClick).withReportText("click"));
	}

}
