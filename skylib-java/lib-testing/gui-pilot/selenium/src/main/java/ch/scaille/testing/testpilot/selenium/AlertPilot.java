package ch.scaille.testing.testpilot.selenium;

import java.util.Optional;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;

import ch.scaille.testing.testpilot.AbstractComponentPilot;
import ch.scaille.testing.testpilot.PollingBuilder;
import ch.scaille.testing.testpilot.PollingContext;

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
	public boolean canCheck(final PollingContext<Alert> ctxt) {
		return false;
	}

	public void doAcknowledge() {
		new PollingBuilder<>(this)
				.fail((context, text) -> "Acknowledging alert: " + context.getComponent().getText())
				.unless()
				.applied((Alert::accept));
	}

}
