package ch.skymarshall.tcwriter.pilot.selenium;

import org.openqa.selenium.WebDriver;

public class GuiPilot extends ch.skymarshall.tcwriter.pilot.GuiPilot {

	private final WebDriver driver;

	public GuiPilot(final WebDriver driver) {
		this.driver = driver;
	}

	public WebDriver getDriver() {
		return driver;
	}
}
