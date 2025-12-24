package ch.scaille.testing.testpilot.swing;

import static ch.scaille.testing.testpilot.factories.Reporting.checkingThat;

import javax.swing.JComponent;

import ch.scaille.testing.testpilot.PollingBuilder;

public class SwingPollingBuilder<C extends JComponent, T extends SwingPollingBuilder<C, T, P>, P extends SwingPollingBuilder.SwingPoller<C>>
		extends PollingBuilder<C, T, P, PollingBuilder.DefaultConfigurer<C>> {

	public static class SwingPoller<C extends JComponent> extends PollingBuilder.Poller<C> {

		protected SwingPoller(PollingBuilder<C, ?, ?, ?> builder) {
			super(builder);
		}

		public boolean enabled() {
			return configure(polling -> polling.reportText(checkingThat("component is enabled")))
					.satisfied(JComponent::isEnabled);
		}

		public boolean disabled() {
			return configure(polling -> polling.reportText(checkingThat("component is disabled")))
					.satisfied(c -> !c.isEnabled());
		}
	}

	public SwingPollingBuilder(SwingComponentPilot<C> elementPilot) {
		super(elementPilot);
	}

	public void assertEnabled() {
		failUnless().enabled();
	}

	public void assertDisabled() {
		failUnless().disabled();
	}

}
