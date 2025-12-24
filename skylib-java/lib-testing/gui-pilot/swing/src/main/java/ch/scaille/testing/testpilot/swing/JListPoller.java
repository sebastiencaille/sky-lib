package ch.scaille.testing.testpilot.swing;

import static ch.scaille.testing.testpilot.factories.PollingResults.failure;
import static ch.scaille.testing.testpilot.factories.PollingResults.success;
import static ch.scaille.testing.testpilot.factories.Reporting.checkingValue;

import javax.swing.JList;

import org.junit.jupiter.api.Assertions;

import ch.scaille.testing.testpilot.Polling;

public class JListPoller extends SwingPollingBuilder<JList, JListPoller, JListPoller.SwingPoller> {

	public static class SwingPoller extends ch.scaille.testing.testpilot.swing.SwingPollingBuilder.SwingPoller<JList> {

		protected SwingPoller(JListPoller builder) {
			super(builder);
		}

		/**
		 * Select a value in a list, according to its String representation
		 */
		public void select(final String value) {
			if (value == null) {
				return;
			}
			configure(polling -> polling.reportText("selecting element " + value)).appliedCtxt(ctxt -> {
				final var c = ctxt.component();
				for (int i = 0; i < c.getModel().getSize(); i++) {
					if (value.equals(c.getModel().getElementAt(i).toString())) {
						c.setSelectedIndex(i);
					}
				}
				Assertions.assertTrue(ctxt.componentPilot().getCachedElement().map(JList::getSelectedIndex).orElse(-1) >= 0,
						() -> ctxt.component().getName() + ": element must have been selected: " + value);
			});
		}

		public void assertSelected(final String expected) {
			if (expected == null) {
				return;
			}
			configure(polling -> polling.reportText(checkingValue(expected)))
					.satisfied(Polling.of(ctxt -> ctxt.componentPilot().canCheck(ctxt), ctxt -> {
						final var component = ctxt.component();
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

	public JListPoller(SwingPilot pilot, String name) {
		super(new SwingComponentPilot<>(pilot, JList.class, name));
	}

	@Override
	protected SwingPoller createPoller() {
		return new SwingPoller(this);
	}

}
