package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.factories.Reporting.checkingThat;

import javax.swing.AbstractButton;
import javax.swing.JToggleButton;

public class JToggleButtonPoller
		extends SwingPollingBuilder<JToggleButton, JToggleButtonPoller, JToggleButtonPoller.SwingPoller> {

	public static class SwingPoller
			extends ch.scaille.tcwriter.pilot.swing.SwingPollingBuilder.SwingPoller<JToggleButton> {

		protected SwingPoller(JToggleButtonPoller builder) {
			super(builder);
		}

		public boolean isSelected(final boolean expected) {
			return configure(polling -> polling
					.withReportText(checkingThat("component is " + (expected ? "selected" : "not selected"))))
					.satisfied(AbstractButton::isSelected);
		}

		public boolean setSelected(final boolean selected) {
			return configure(polling -> polling.withReportText(selected ? "selecting" : "deselecting"))
					.applied(c -> c.setSelected(selected));
		}
	}

	public JToggleButtonPoller(SwingPilot pilot, String name) {
		super(new SwingComponentPilot<>(pilot, JToggleButton.class, name));
	}

	@Override
	protected SwingPoller createPoller() {
		return new SwingPoller(this);
	}

}
