package ch.scaille.tcwriter.pilot.swing;

import javax.swing.JButton;

public class JButtonPoller extends SwingPollingBuilder<JButton, JButtonPoller, JButtonPoller.SwingPoller> {

	public static class SwingPoller extends ch.scaille.tcwriter.pilot.swing.SwingPollingBuilder.SwingPoller<JButton> {

		protected SwingPoller(JButtonPoller builder) {
			super(builder);
		}

		public void click() {
			configure(polling -> polling.withReportText("click")).applied(JButton::doClick);
		}

	}

	public JButtonPoller(SwingPilot pilot, String name) {
		super(new SwingComponentPilot<>(pilot, JButton.class, name));
	}

	@Override
	protected SwingPoller createPoller() {
		return new SwingPoller(this);
	}

}
