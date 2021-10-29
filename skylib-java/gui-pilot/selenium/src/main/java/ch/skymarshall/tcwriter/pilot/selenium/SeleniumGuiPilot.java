package ch.skymarshall.tcwriter.pilot.selenium;

import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import ch.skymarshall.tcwriter.pilot.ModalDialogDetector;
import ch.skymarshall.tcwriter.pilot.ModalDialogDetector.PollingResult;

public class SeleniumGuiPilot extends ch.skymarshall.tcwriter.pilot.GuiPilot {

	private final WebDriver driver;

	public SeleniumGuiPilot(final WebDriver driver) {
		this.driver = driver;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public ElementPilot element(final By locator) {
		return new ElementPilot(this, locator);
	}

	public AlertPilot alert() {
		return new AlertPilot(this);
	}

	@Override
	protected ModalDialogDetector createDefaultModalDialogDetector() {
		return new ModalDialogDetector(() -> AlertDetector.listAlerts(this, null));
	}

	public void expectModalDialog(final Function<AlertPilot, PollingResult> check) {
		setCurrentModalDialogDetector(new ModalDialogDetector(() -> AlertDetector.listAlerts(this, check)));
	}


}
