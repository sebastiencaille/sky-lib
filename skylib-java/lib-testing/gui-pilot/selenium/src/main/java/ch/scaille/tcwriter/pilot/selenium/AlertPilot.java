package ch.scaille.tcwriter.pilot.selenium;

import java.util.Optional;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;

import ch.scaille.tcwriter.pilot.AbstractComponentPilot;
import ch.scaille.tcwriter.pilot.Factories.Pollings;

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
	protected boolean canCheck(final Alert component) {
		return false;
	}

	@Override
	protected boolean canEdit(final Alert component) {
		return false;
	}

	public void doAcknowledge() {
		polling(Pollings.applyOnExisting(Alert::accept)
				.withReportFunction((cp, t) -> "Acknowledging alert: " + cp.component.getText())).orFail();
	}

}
