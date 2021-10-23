package ch.skymarshall.tcwriter.pilot.swing;

import javax.swing.JLabel;

public class JLabelPilot extends AbstractSwingComponent<JLabelPilot, JLabel> {

	public JLabelPilot(final SwingPilot pilot, final String name) {
		super(pilot, JLabel.class, name);
	}

	public void checkValue(final String expected) {
		wait(assertEquals("check text", expected, JLabel::getText));
	}

}
