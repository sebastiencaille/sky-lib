package ch.scaille.tcwriter.pilot.selenium;

import java.util.Optional;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;

import ch.scaille.tcwriter.pilot.AbstractComponentPilot;
import ch.scaille.tcwriter.pilot.PollingBuilder;
import ch.scaille.tcwriter.pilot.PollingContext;
import ch.scaille.tcwriter.pilot.PollingBuilder.Poller;

public class AlertPilot extends AbstractComponentPilot<AlertPilot, Alert> {

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
	protected boolean canCheck(final PollingContext<Alert> ctxt) {
		return false;
	}

	public void doAcknowledge() {
		polling().fail((context, text) -> "Acknowledging alert: " + context.getComponent().getText())
				.ifNot()
				.applied((Alert::accept));
	}

}
