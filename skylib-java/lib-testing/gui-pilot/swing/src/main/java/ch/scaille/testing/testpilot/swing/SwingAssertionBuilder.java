package ch.scaille.testing.testpilot.swing;

import static ch.scaille.testing.testpilot.factories.Reporting.checkingThat;

import javax.swing.JComponent;

import ch.scaille.testing.testpilot.builder.AssertionBuilder;
import ch.scaille.testing.testpilot.builder.AssertionChain;
import ch.scaille.testing.testpilot.builder.AssertionResult;
import ch.scaille.testing.testpilot.builder.DefaultConfigurer;

public class SwingAssertionBuilder<C extends JComponent, T extends SwingAssertionBuilder<C, T, P>, P extends SwingAssertionBuilder.SwingAssertion<C>>
		extends AssertionBuilder<C, T, P, DefaultConfigurer<C>> {

	public static class SwingAssertion<C extends JComponent> extends AssertionChain<C> {

		protected SwingAssertion(AssertionBuilder<C, ?, ?, ?> builder) {
			super(builder);
		}

		public AssertionResult<C> enabled() {
			return configure(polling -> polling.reporting(checkingThat("component is enabled")))
					.satisfied(JComponent::isEnabled);
		}

		public AssertionResult<C> disabled() {
			return configure(polling -> polling.reporting(checkingThat("component is disabled")))
					.satisfied(c -> !c.isEnabled());
		}
	}

	public SwingAssertionBuilder(SwingComponentPilot<C> elementPilot) {
		super(elementPilot);
	}

	public void assertEnabled() {
		failUnless().enabled();
	}

	public void assertDisabled() {
		failUnless().disabled();
	}

}
