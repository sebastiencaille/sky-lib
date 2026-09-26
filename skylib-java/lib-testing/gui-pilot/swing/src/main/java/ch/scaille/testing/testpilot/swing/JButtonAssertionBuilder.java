package ch.scaille.testing.testpilot.swing;

import static ch.scaille.testing.testpilot.factories.Reporting.text;

import javax.swing.JButton;

import ch.scaille.testing.testpilot.builder.AssertionResult;

public class JButtonAssertionBuilder extends SwingAssertionBuilder<JButton, JButtonAssertionBuilder, JButtonAssertionBuilder.SwingAssertion> {

	public static class SwingAssertion extends SwingAssertionBuilder.SwingAssertion<JButton> {

		protected SwingAssertion(JButtonAssertionBuilder builder) {
			super(builder);
		}

		public AssertionResult<JButton> click() {
			return configure(polling -> polling.reporting(text("click"))).applied(JButton::doClick);
		}

	}

	public JButtonAssertionBuilder(SwingPilot pilot, String name) {
		super(new SwingComponentPilot<>(pilot, JButton.class, name));
	}

	@Override
	protected SwingAssertion createPoller() {
		return new SwingAssertion(this);
	}

}
