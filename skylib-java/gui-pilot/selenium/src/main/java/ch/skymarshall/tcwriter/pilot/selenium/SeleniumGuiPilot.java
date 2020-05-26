package ch.skymarshall.tcwriter.pilot.selenium;

import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import ch.skymarshall.tcwriter.pilot.ModalDialogDetector;
import ch.skymarshall.tcwriter.pilot.ModalDialogDetector.ErrorCheck;

public class SeleniumGuiPilot extends ch.skymarshall.tcwriter.pilot.GuiPilot {

	private final WebDriver driver;

	public SeleniumGuiPilot(final WebDriver driver) {
		this.driver = driver;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public SeleniumElement element(final By locator) {
		return new SeleniumElement(this, locator);
	}

	public SeleniumAlert alert() {
		return new SeleniumAlert(this);
	}

	@Override
	protected ModalDialogDetector createDefaultModalDialogDetector() {
		return new ModalDialogDetector(() -> AlertDetector.listAlerts(this, null));
	}

	public void expectModalDialog(final Function<SeleniumAlert, ErrorCheck> check) {
		setCurrentModalDialogDetector(new ModalDialogDetector(() -> AlertDetector.listAlerts(this, check)));
	}

}
