package ch.skymarshall.tcwriter.pilot.swing;

import javax.swing.JButton;

public class SwingButton extends AbstractSwingComponent<SwingButton, JButton> {

	public SwingButton(final SwingGuiPilot pilot, final String name) {
		super(pilot, JButton.class, name);
	}

	public void click() {
		wait(action(JButton::doClick).withReport(c -> "click"));
	}

}
