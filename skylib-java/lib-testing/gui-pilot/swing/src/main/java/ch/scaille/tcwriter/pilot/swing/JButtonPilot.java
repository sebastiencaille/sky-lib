package ch.scaille.tcwriter.pilot.swing;

import javax.swing.JButton;

public class JButtonPilot extends AbstractSwingComponentPilot<JButton> {

	public JButtonPilot(final SwingPilot pilot, final String name) {
		super(pilot, JButton.class, name);
	}

	public void click() {
		polling().fail("click").ifNot().applied(JButton::doClick);
	}

}
