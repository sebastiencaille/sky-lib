package ch.scaille.testing.testpilot.swing;

import static ch.scaille.testing.testpilot.factories.Reporting.checkingValue;

import javax.swing.JLabel;

import ch.scaille.testing.testpilot.builder.AssertionResult;

public class JLabelAssertionBuilder extends SwingAssertionBuilder<JLabel, JLabelAssertionBuilder, JLabelAssertionBuilder.SwingAssertion> {

	public static class SwingAssertion extends SwingAssertionBuilder.SwingAssertion<JLabel> {

		protected SwingAssertion(JLabelAssertionBuilder builder) {
			super(builder);
		}

		public AssertionResult<JLabel> assertTextEquals(final String expected) {
			return configure(polling -> polling.reporting(checkingValue(expected)))
					.assertThat(AssertjAsserts.textEquals(expected,  JLabel::getText));
		}

	}

	public JLabelAssertionBuilder(SwingPilot pilot, String name) {
		super(new SwingComponentPilot<>(pilot, JLabel.class, name));
	}

	@Override
	protected SwingAssertion createPoller() {
		return new SwingAssertion(this);
	}

}
