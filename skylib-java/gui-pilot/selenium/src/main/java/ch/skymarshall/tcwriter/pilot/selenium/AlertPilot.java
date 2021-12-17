package ch.skymarshall.tcwriter.pilot.selenium;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;

import ch.skymarshall.tcwriter.pilot.AbstractGuiComponent;
import ch.skymarshall.tcwriter.pilot.Factories;

public class AlertPilot extends AbstractGuiComponent<AlertPilot, Alert> {

	private final SeleniumPilot pilot;

	public AlertPilot(final SeleniumPilot pilot) {
		super(pilot);
		this.pilot = pilot;
	}

	@Override
	protected Alert loadGuiComponent() {
		try {
			return pilot.getDriver().switchTo().alert();
		} catch (final NoAlertPresentException e) {
			return null;
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
		wait(Factories.success(Alert::accept).withReportFunction((cp, t) -> "Acknowledging alert: " + cp.component.getText()));
	}

}
