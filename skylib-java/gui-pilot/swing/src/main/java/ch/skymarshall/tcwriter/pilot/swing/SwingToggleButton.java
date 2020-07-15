package ch.skymarshall.tcwriter.pilot.swing;

import javax.swing.JToggleButton;

import org.junit.Assert;

public class SwingToggleButton extends AbstractSwingComponent<SwingToggleButton, JToggleButton> {

	public SwingToggleButton(final SwingGuiPilot pilot, final String name) {
		super(pilot, JToggleButton.class, name);
	}

	public void waitSelected(final boolean selected) {
		wait(assertion(c -> Assert.assertEquals(selected, c.isSelected()))
				.withReport(c -> "check " + (selected ? "selected" : "not selected")));
	}

	public void setSelected(final boolean selected) {
		wait(action(c -> c.setSelected(selected)).withReport(c -> "set " + (selected ? "selected" : "not selected")));
	}

}
