package ch.scaille.testing.testpilot.swing;

import javax.swing.JLabel;

import org.junit.jupiter.api.Assertions;

import ch.scaille.testing.testpilot.factories.Reporting;

public class JLabelPoller extends SwingPollingBuilder<JLabel, JLabelPoller, JLabelPoller.SwingPoller> {

	public static class SwingPoller extends ch.scaille.testing.testpilot.swing.SwingPollingBuilder.SwingPoller<JLabel> {

		protected SwingPoller(JLabelPoller builder) {
			super(builder);
		}

		public void assertTextEquals(final String expected) {
			configure(polling -> polling.withReportText(Reporting.checkingValue(expected))).assertedCtxt(
					pc -> Assertions.assertEquals(expected, pc.getComponent().getText(), pc.getDescription()));
		}

	}

	public JLabelPoller(SwingPilot pilot, String name) {
		super(new SwingComponentPilot<>(pilot, JLabel.class, name));
	}

	@Override
	protected SwingPoller createPoller() {
		return new SwingPoller(this);
	}

}
