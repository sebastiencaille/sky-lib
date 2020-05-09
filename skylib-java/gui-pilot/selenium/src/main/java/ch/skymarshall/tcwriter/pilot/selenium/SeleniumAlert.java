package ch.skymarshall.tcwriter.pilot.selenium;

import static ch.skymarshall.tcwriter.pilot.Polling.action;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;

import ch.skymarshall.tcwriter.pilot.AbstractGuiComponent;
import ch.skymarshall.tcwriter.pilot.Polling;

public class SeleniumAlert extends AbstractGuiComponent<Alert, SeleniumAlert> {

	private final GuiPilot pilot;

	public SeleniumAlert(final GuiPilot pilot) {
		super(pilot);
		this.pilot = pilot;
	}

	@Override
	protected Alert loadElement() {
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

}
