package ch.skymarshall.tcwriter.pilot.selenium;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;

import ch.skymarshall.tcwriter.pilot.AbstractGuiComponent;

public class SeleniumAlert extends AbstractGuiComponent<Alert> {

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
		addReporting(e -> "Acknowledge alert: " + e.getText());
		waitActionSuccess(null, action(Alert::accept), pilot.getDefaultActionTimeout(),
				assertFail("unable to acknowledge alert"));
		return this;
	}

}
