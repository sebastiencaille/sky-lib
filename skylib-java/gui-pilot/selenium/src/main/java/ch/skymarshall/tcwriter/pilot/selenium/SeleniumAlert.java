package ch.skymarshall.tcwriter.pilot.selenium;

import static ch.skymarshall.tcwriter.pilot.Polling.action;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;

import ch.skymarshall.tcwriter.pilot.AbstractGuiComponent;
import ch.skymarshall.tcwriter.pilot.Polling;

public class SeleniumAlert extends AbstractGuiComponent<SeleniumAlert, Alert> {

	private final SeleniumGuiPilot pilot;

	public SeleniumAlert(final SeleniumGuiPilot pilot) {
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

	public SeleniumAlert acknowledge() {
		withReport(e -> "Acknowledged alert: " + e.getText());
		waitActionSuccess(null, action(Alert::accept), pilot.getDefaultActionTimeout(),
				Polling.assertFail("unable to acknowledge alert"));
		return this;
	}

	@Override
	protected boolean canCheck(final Alert component) {
		return false;
	}

	@Override
	protected boolean canEdit(final Alert component) {
		return false;
	}

}
