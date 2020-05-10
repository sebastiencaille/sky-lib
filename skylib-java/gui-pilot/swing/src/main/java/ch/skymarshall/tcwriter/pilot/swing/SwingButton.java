package ch.skymarshall.tcwriter.pilot.swing;

import static ch.skymarshall.tcwriter.pilot.Polling.action;

import javax.swing.JButton;

public class SwingButton extends AbstractSwingComponent<JButton> {

	public SwingButton(final SwingGuiPilot pilot, final String name) {
		super(pilot, JButton.class, name);
	}

	public void click() {
		withReport(c -> "click").waitEditSuccess(action(JButton::doClick));
	}

}
