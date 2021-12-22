package ch.scaille.tcwriter.pilot.selenium;

import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import ch.scaille.tcwriter.pilot.ModalDialogDetector;
import ch.scaille.tcwriter.pilot.ModalDialogDetector.PollingResult;

public class SeleniumPilot extends ch.scaille.tcwriter.pilot.GuiPilot {

	private final WebDriver driver;

	public SeleniumPilot(final WebDriver driver) {
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

	public <C extends PagePilot> C page(Function<SeleniumPilot, C> factory) {
		return factory.apply(this);
	}

	@Override
	protected ModalDialogDetector createDefaultModalDialogDetector() {
		return new ModalDialogDetector(() -> AlertDetector.listAlerts(this, null));
	}

	public ModalDialogDetector expectModalDialog(final Function<AlertPilot, PollingResult> check) {
		return expectModalDialog(new ModalDialogDetector(() -> AlertDetector.listAlerts(this, check)));
	}

}
