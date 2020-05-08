package ch.skymarshall.tcwriter.pilot.swing;

import javax.swing.JList;

import org.junit.Assert;

public class SwingJList extends AbstractSwingComponent<JList> {

	public SwingJList(final GuiPilot pilot, final String name) {
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
		addReporting(r -> "Select element " + value + " of " + clazz.getSimpleName() + " " + name);
		waitComponentEditSuccess(action(l -> {
			for (int i = 0; i < l.getModel().getSize(); i++) {
				if (value.equals(l.getModel().getElementAt(i).toString())) {
					l.setSelectedIndex(i);
				}
			}
		}), assertFail());
		Assert.assertTrue("Value [" + name + ":" + value + "] must have been selected",
				getCachedElement().getSelectedIndex() >= 0);
	}

	public void checkSelected(final String value) {
		if (value == null) {
			return;
		}
		addReporting(r -> "Check element " + value + " is selected in " + clazz.getSimpleName() + " " + name);
		waitComponentEditSuccess(l -> {
			if (l.getSelectedIndex() < 0) {
				return failure("No element selected");
			}
			final String current = l.getModel().getElementAt(l.getSelectedIndex()).toString();
			if (!value.equals(current)) {
				return failure("Wrong element selected (" + current + ")");
			}
			return value(true);
		}, assertFail());
	}
}
