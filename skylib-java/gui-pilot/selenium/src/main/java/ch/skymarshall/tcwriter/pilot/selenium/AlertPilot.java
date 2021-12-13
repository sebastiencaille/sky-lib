package ch.skymarshall.tcwriter.pilot.selenium;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;

import ch.skymarshall.tcwriter.pilot.AbstractGuiComponent;
import ch.skymarshall.tcwriter.pilot.Polling;

public class AlertPilot extends AbstractGuiComponent<AlertPilot, Alert> {

	private final SeleniumGuiPilot pilot;

	public AlertPilot(final SeleniumGuiPilot pilot) {
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
		wait(Polling.success(Alert::accept).withReportText("acknowledge alert")
				.withReportFunction((e, s, t) -> "Acknowledged alert: " + e.getText()));
	}

}
