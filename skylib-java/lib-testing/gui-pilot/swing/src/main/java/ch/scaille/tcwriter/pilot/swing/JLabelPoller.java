package ch.scaille.tcwriter.pilot.swing;

import javax.swing.JLabel;

import org.junit.jupiter.api.Assertions;

import ch.scaille.tcwriter.pilot.factories.Reporting;

public class JLabelPoller extends SwingPollingBuilder<JLabel, JLabelPoller, JLabelPoller.SwingPoller> {

	public static class SwingPoller extends ch.scaille.tcwriter.pilot.swing.SwingPollingBuilder.SwingPoller<JLabel> {

		protected SwingPoller(JLabelPoller builder) {
			super(builder);
		}

		public void textEquals(final String expected) {
			configure(polling -> polling.withReportText(Reporting.checkingValue(expected))).asserted(
					pc -> Assertions.assertEquals(expected, pc.getComponent().getText(), pc.getDescription()));
		}

	}

	public JLabelPoller(SwingPilot pilot, String name) {
		super(new SwingComponentPilot<>(pilot, JLabel.class, name));
	}

	@Override
	public SwingPoller ifNot() {
		return new SwingPoller(this);
	}

	public void assertTextEquals(final String expected) {
		fail().ifNot().textEquals(expected);
	}

}
