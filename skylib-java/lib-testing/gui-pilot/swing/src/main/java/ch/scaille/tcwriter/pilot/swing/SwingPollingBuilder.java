package ch.scaille.tcwriter.pilot.swing;

import javax.swing.JComponent;

import ch.scaille.tcwriter.pilot.PollingBuilder;
import ch.scaille.tcwriter.pilot.factories.Pollings;
import ch.scaille.tcwriter.pilot.factories.Reporting;

public class SwingPollingBuilder<G extends AbstractSwingComponent<G, C>, C extends JComponent>
		extends PollingBuilder<G, C> {

	public SwingPollingBuilder(AbstractSwingComponent<G, C> elementPilot) {
		super(elementPilot);
	}

	public void assertEnabled() {
		poll(Pollings.<C>satisfies(JComponent::isEnabled)
				.withReportText(Reporting.checkingThat("component is enabled"))).orFail();
	}

	public void assertDisabled() {
		poll(Pollings.<C>satisfies(c -> !c.isEnabled()).withReportText(Reporting.checkingThat("component is disabled")))
				.orFail();
	}

}
