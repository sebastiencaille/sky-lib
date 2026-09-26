package ch.scaille.testing.testpilot.swing;

import static ch.scaille.testing.testpilot.factories.PollingResults.failure;
import static ch.scaille.testing.testpilot.factories.PollingResults.success;
import static ch.scaille.testing.testpilot.factories.Reporting.checkingValue;
import static ch.scaille.testing.testpilot.factories.Reporting.text;

import javax.swing.JList;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Assertions;

import ch.scaille.testing.testpilot.Polling;
import ch.scaille.testing.testpilot.builder.AssertionResult;

public class JListAssertionBuilder
		extends SwingAssertionBuilder<JList, JListAssertionBuilder, JListAssertionBuilder.SwingAssertion> {

	public static class SwingAssertion extends SwingAssertionBuilder.SwingAssertion<JList> {

		protected SwingAssertion(JListAssertionBuilder builder) {
			super(builder);
		}

		/**
		 * Select a value in a list, according to its String representation
		 */
		public AssertionResult<JList> select(final @Nullable String value) {
			if (value == null) {
				return new AssertionResult<>(this, false);
			}
			return configure(polling -> polling.reporting(text("selecting element " + value))).appliedCtxt(ctxt -> {
				final var c = ctxt.component();
				for (int i = 0; i < c.getModel().getSize(); i++) {
					if (value.equals(c.getModel().getElementAt(i).toString())) {
						c.setSelectedIndex(i);
					}
				}
				Assertions.assertTrue(
						ctxt.componentPilot().getCachedElement().map(JList::getSelectedIndex).orElse(-1) >= 0,
						() -> ctxt.component().getName() + ": element must have been selected: " + value);
			});
		}

		public AssertionResult<JList> assertSelected(final @Nullable String expected) {
			if (expected == null) {
				return new AssertionResult<>(this, false);
			}
			return configure(polling -> polling.reporting(checkingValue(expected)))
					.testAssertion(Polling.of(ctxt -> ctxt.componentPilot().canCheck(ctxt), ctxt -> {
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

	public JListAssertionBuilder(SwingPilot pilot, String name) {
		super(new SwingComponentPilot<>(pilot, JList.class, name));
	}

	@Override
	protected SwingAssertion createPoller() {
		return new SwingAssertion(this);
	}

}
