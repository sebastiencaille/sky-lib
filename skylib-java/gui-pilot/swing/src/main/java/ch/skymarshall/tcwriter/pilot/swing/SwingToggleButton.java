package ch.skymarshall.tcwriter.pilot.swing;

import static ch.skymarshall.tcwriter.pilot.Polling.action;
import static ch.skymarshall.tcwriter.pilot.Polling.assertion;

import javax.swing.JToggleButton;

import org.junit.Assert;

public class SwingToggleButton extends AbstractSwingComponent<JToggleButton> {

	public SwingToggleButton(final GuiPilot pilot, final String name) {
		super(pilot, JToggleButton.class, name);
	}

	public void waitSelected(final boolean selected) {
		withReport(c -> "check " + (selected ? "selected" : "not selected"))
				.waitReadSuccess(assertion(c -> Assert.assertEquals(selected, c.isSelected())));
	}

	public void setSelected(final boolean selected) {
		withReport(c -> "set " + (selected ? "selected" : "not selected"))
				.waitEditSuccess(action(c -> c.setSelected(selected)));
	}

}
