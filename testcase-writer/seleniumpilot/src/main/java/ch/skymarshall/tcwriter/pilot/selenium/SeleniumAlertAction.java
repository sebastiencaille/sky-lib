package ch.skymarshall.tcwriter.pilot.selenium;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;

import ch.skymarshall.tcwriter.pilot.AbstractGuiAction;

public class SeleniumAlertAction extends AbstractGuiAction<Alert> {

	private final GuiPilot pilot;

	public SeleniumAlertAction(final GuiPilot pilot) {
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

	public SeleniumAlertAction acknowledge() {
		addReporting(e -> "Acknowledge alert " + e.getText());
		waitActionSuccess(null, consumer(Alert::accept), pilot.getDefaultActionTimeout());
		return this;
	}

}
