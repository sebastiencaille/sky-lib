package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.factories.PollingResults.failure;
import static ch.scaille.tcwriter.pilot.factories.PollingResults.success;
import static ch.scaille.tcwriter.pilot.factories.Reporting.checkingValue;

import javax.swing.JList;

import org.junit.jupiter.api.Assertions;

import ch.scaille.tcwriter.pilot.Polling;

@SuppressWarnings("java:S5960")
public class JListPilot extends AbstractSwingComponentPilot<JList> {

	public JListPilot(final SwingPilot pilot, final String name) {
		super(pilot, JList.class, name);
	}

	/**
	 * Select a value in a list, according to its String representation
	 */
	public void select(final String value) {
		if (value == null) {
			return;
		}
		polling().fail("selecting element " + value).ifNot().applied(c -> {
			for (int i = 0; i < c.getModel().getSize(); i++) {
				if (value.equals(c.getModel().getElementAt(i).toString())) {
					c.setSelectedIndex(i);
				}
			}
		});
		Assertions.assertTrue(getCachedElement().map(JList::getSelectedIndex).orElse(-1) >= 0,
				() -> name + ": element must have been selected: " + value);
	}

	public void assertSelected(final String expected) {
		if (expected == null) {
			return;
		}
		polling().fail(checkingValue(expected)).ifNot().satisfied(new Polling<>(this::canCheck, ctxt -> {
			final var component = ctxt.getComponent();
			if (component.getSelectedIndex() < 0) {
				return failure("No element selected");
			}
			final var current = component.getModel().getElementAt(component.getSelectedIndex()).toString();
			if (!expected.equals(current)) {
				return failure("Wrong element selected (" + current + ")");
			}
			return success();
		}));
	}
}
