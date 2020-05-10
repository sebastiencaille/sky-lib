package ch.skymarshall.tcwriter.pilot.swing;

import static ch.skymarshall.tcwriter.pilot.Polling.action;
import static ch.skymarshall.tcwriter.pilot.Polling.failure;
import static ch.skymarshall.tcwriter.pilot.Polling.isTrue;

import javax.swing.JList;

import org.junit.Assert;

public class SwingList extends AbstractSwingComponent<JList> {

	public SwingList(final SwingGuiPilot pilot, final String name) {
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
		withReport(r -> "select element " + value).waitEditSuccess(action(l -> {
			for (int i = 0; i < l.getModel().getSize(); i++) {
				if (value.equals(l.getModel().getElementAt(i).toString())) {
					l.setSelectedIndex(i);
				}
			}
		}));
		Assert.assertTrue("Value [" + name + ":" + value + "] must have been selected",
				getCachedElement().getSelectedIndex() >= 0);
	}

	public void checkSelected(final String value) {
		if (value == null) {
			return;
		}
		withReport(r -> "check element " + value).waitReadSuccess(l -> {
			if (l.getSelectedIndex() < 0) {
				return failure("No element selected");
			}
			final String current = l.getModel().getElementAt(l.getSelectedIndex()).toString();
			if (!value.equals(current)) {
				return failure("Wrong element selected (" + current + ")");
			}
			return isTrue();
		});
	}
}
