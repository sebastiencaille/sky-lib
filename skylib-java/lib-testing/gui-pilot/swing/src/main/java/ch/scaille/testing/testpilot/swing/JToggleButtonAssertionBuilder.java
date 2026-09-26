package ch.scaille.testing.testpilot.swing;

import ch.scaille.testing.testpilot.builder.AssertionResult;

import static ch.scaille.testing.testpilot.factories.Reporting.checkingThat;
import static ch.scaille.testing.testpilot.factories.Reporting.text;

import javax.swing.AbstractButton;
import javax.swing.JToggleButton;

public class JToggleButtonAssertionBuilder
		extends SwingAssertionBuilder<JToggleButton, JToggleButtonAssertionBuilder, JToggleButtonAssertionBuilder.SwingAssertion> {

	public static class SwingAssertion
			extends SwingAssertionBuilder.SwingAssertion<JToggleButton> {

		protected SwingAssertion(JToggleButtonAssertionBuilder builder) {
			super(builder);
		}

		public AssertionResult<JToggleButton> isSelected(final boolean expected) {
			return configure(polling -> polling.reporting(checkingThat("component is " + (expected ? "selected" : "not selected"))))
					.satisfied(AbstractButton::isSelected);
		}

		public AssertionResult<JToggleButton> setSelected(final boolean selected) {
			return configure(polling -> polling.reporting(text(selected ? "selecting" : "deselecting")))
					.applied(c -> c.setSelected(selected));
		}
	}

	public JToggleButtonAssertionBuilder(SwingPilot pilot, String name) {
		super(new SwingComponentPilot<>(pilot, JToggleButton.class, name));
	}

	@Override
	protected SwingAssertion createPoller() {
		return new SwingAssertion(this);
	}

}
