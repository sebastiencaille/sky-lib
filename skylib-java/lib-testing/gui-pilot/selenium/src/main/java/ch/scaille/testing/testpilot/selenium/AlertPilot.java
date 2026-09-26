package ch.scaille.testing.testpilot.selenium;

import java.util.Optional;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;

import ch.scaille.testing.testpilot.AbstractComponentPilot;
import ch.scaille.testing.testpilot.PolledComponent;
import ch.scaille.testing.testpilot.builder.AssertionBuilder;
import ch.scaille.testing.testpilot.factories.Reporting;


public class AlertPilot extends AbstractComponentPilot<Alert> {

	private final SeleniumPilot pilot;

	public AlertPilot(final SeleniumPilot pilot) {
		super(pilot);
		this.pilot = pilot;
	}

	@Override
	protected Optional<Alert> loadGuiComponent() {
		try {
			return Optional.of(pilot.getDriver().switchTo().alert());
		} catch (final NoAlertPresentException e) {
			return Optional.empty();
		}
	}

	@Override
	protected Optional<String> getDescription() {
		return getCachedElement().map(Alert::getText);
	}

	@Override
	public boolean canCheck(final PolledComponent<Alert> ctxt) {
		return false;
	}

	public void doAcknowledge() {
		new AssertionBuilder<>(this)
				.failWith(Reporting.formatted( "Acknowledging alert: %s", poller -> poller.component().getText()))
				.unless()
				.applied((Alert::accept));
	}

}
