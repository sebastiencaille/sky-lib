package ch.scaille.tcwriter.pilot.swing;

import javax.swing.JComponent;

import ch.scaille.tcwriter.pilot.PollingBuilder;
import ch.scaille.tcwriter.pilot.factories.Pollings;
import ch.scaille.tcwriter.pilot.factories.Reporting;

public class SwingPollingBuilder<G extends AbstractSwingComponentPilot<G, C>, C extends JComponent>
		extends PollingBuilder<G, C> {

	public SwingPollingBuilder(AbstractSwingComponentPilot<G, C> elementPilot) {
		super(elementPilot);
	}

	public void assertEnabled() {
		pollOrFail(Pollings.<C>satisfies(JComponent::isEnabled)
				.withReportText(Reporting.checkingThat("component is enabled")));
	}

	public void assertDisabled() {
		pollOrFail(Pollings.<C>satisfies(c -> !c.isEnabled())
				.withReportText(Reporting.checkingThat("component is disabled")));
	}

}
