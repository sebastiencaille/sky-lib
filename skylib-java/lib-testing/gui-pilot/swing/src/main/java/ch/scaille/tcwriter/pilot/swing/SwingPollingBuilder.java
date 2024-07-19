package ch.scaille.tcwriter.pilot.swing;

import static ch.scaille.tcwriter.pilot.factories.Reporting.checkingThat;

import javax.swing.JComponent;

import ch.scaille.tcwriter.pilot.PollingBuilder;

public class SwingPollingBuilder<C extends JComponent>
		extends PollingBuilder<C, SwingPollingBuilder<C>, SwingPollingBuilder.SwingPoller<C>> {

	public static class SwingPoller<C extends JComponent> extends PollingBuilder.Poller<C> {

		protected SwingPoller(PollingBuilder<C, ?, ?> builder) {
			super(builder);
		}

		public boolean enabled() {
			return configure(polling -> polling.withReportText(checkingThat("component is enabled")))
					.satisfied(JComponent::isEnabled);
		}

		public boolean disabled() {
			return configure(polling -> polling.withReportText(checkingThat("component is disabled")))
					.satisfied(c -> !c.isEnabled());
		}
	}

	public SwingPollingBuilder(AbstractSwingComponentPilot<C> elementPilot) {
		super(elementPilot);
	}

	@Override
	public SwingPoller<C> ifNot() {
		return new SwingPoller<>(this);
	}

}
