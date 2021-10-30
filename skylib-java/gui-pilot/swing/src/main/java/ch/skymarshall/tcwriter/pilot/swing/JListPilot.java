package ch.skymarshall.tcwriter.pilot.swing;

import javax.swing.JList;

import org.junit.jupiter.api.Assertions;

import ch.skymarshall.tcwriter.pilot.Polling;
import ch.skymarshall.tcwriter.pilot.PollingResult;

@SuppressWarnings("java:S5960")
public class JListPilot extends AbstractSwingComponent<JListPilot, JList> {

	public JListPilot(final SwingPilot pilot, final String name) {
		super(pilot, JList.class, name);
	}

	/**
	 * Select a value in a list, according to it's String representation
	 *
	 * @param componentName
	 * @param value
	 */
	public void select(final String value) {
		if (value == null) {
			return;
		}
		wait(action(c -> {
			for (int i = 0; i < c.getModel().getSize(); i++) {
				if (value.equals(c.getModel().getElementAt(i).toString())) {
					c.setSelectedIndex(i);
				}
			}
		}).withReport(r -> "select element " + value));
		Assertions.assertTrue(getCachedElement().getSelectedIndex() >= 0,
				() -> "Value [" + name + ":" + value + "] must have been selected");
	}

	public void checkSelected(final String value) {
		if (value == null) {
			return;
		}
		wait(new Polling<>(this::canCheck, c -> {
			if (c.getSelectedIndex() < 0) {
				return PollingResult.failure("No element selected");
			}
			final String current = c.getModel().getElementAt(c.getSelectedIndex()).toString();
			if (!value.equals(current)) {
				return PollingResult.failure("Wrong element selected (" + current + ")");
			}
			return PollingResult.success();
		}).withReport(r -> "check element " + value));
	}
}
