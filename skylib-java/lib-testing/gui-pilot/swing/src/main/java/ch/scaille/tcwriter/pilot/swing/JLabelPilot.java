package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.Factories.Reporting.checkingValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.swing.JLabel;

@SuppressWarnings("java:S5960")
public class JLabelPilot extends AbstractSwingComponent<JLabelPilot, JLabel> {

	public JLabelPilot(final SwingPilot pilot, final String name) {
		super(pilot, JLabel.class, name);
	}

	public void checkValue(final String expected) {
		wait(assertion(pc -> assertEquals(expected, pc.component.getText(), pc.description)).withReportText(checkingValue(expected)));
	}

}
