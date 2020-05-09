package ch.skymarshall.tcwriter.pilot.swing;

import javax.swing.JButton;

public class SwingButton extends AbstractSwingComponent<JButton> {

	public SwingButton(final GuiPilot pilot, final String name) {
		super(pilot, JButton.class, name);
	}

	public void click() {
		waitComponentEditSuccess(action(JButton::doClick), assertFail());
	}

}
