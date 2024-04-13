package ch.scaille.tcwriter.pilot.swing;

import javax.swing.JLabel;

import org.junit.jupiter.api.Assertions;

import ch.scaille.tcwriter.pilot.factories.Reporting;

@SuppressWarnings("java:S5960")
public class JLabelPilot extends AbstractSwingComponentPilot<JLabelPilot, JLabel> {

	public JLabelPilot(final SwingPilot pilot, final String name) {
		super(pilot, JLabel.class, name);
	}

	public void assertTextEquals(final String expected) {
		polling().asserts(pc -> Assertions.assertEquals(expected, pc.getComponent().getText(), pc.getDescription()))
				.orFail(Reporting.checkingValue(expected));
	}

}
