package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.Factories.PollingResults.failure;
import static ch.scaille.tcwriter.pilot.Factories.PollingResults.success;
import static ch.scaille.tcwriter.pilot.Factories.Reporting.checkingValue;

import javax.swing.JList;

import org.junit.jupiter.api.Assertions;

import ch.scaille.tcwriter.pilot.Factories;
import ch.scaille.tcwriter.pilot.Polling;
import ch.scaille.tcwriter.pilot.Polling.PollingFunction;

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
		polling(applies(c -> {
			for (int i = 0; i < c.getModel().getSize(); i++) {
				if (value.equals(c.getModel().getElementAt(i).toString())) {
					c.setSelectedIndex(i);
				}
			}
		})).orFail("selecting element " + value);
		Assertions.assertTrue(getCachedElement().map(JList::getSelectedIndex).orElse(-1) >= 0,
				() -> name + ": element must have been selected: " + value);
	}

	public void checkSelected(final String expected) {
		if (expected == null) {
			return;
		}
		polling(new Polling<JList, Boolean>(this::canCheck, pc -> {
			final var component = pc.component;
			if (component.getSelectedIndex() < 0) {
				return failure("No element selected");
			}
			final var current = component.getModel().getElementAt(component.getSelectedIndex()).toString();
			if (!expected.equals(current)) {
				return failure("Wrong element selected (" + current + ")");
			}
			return success();
		})).orFail(checkingValue(expected));
	}
}
