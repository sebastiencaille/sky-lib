package ch.skymarshall.tcwriter.pilot.selenium;

import org.openqa.selenium.WebDriver;

public class SeleniumGuiPilot extends ch.skymarshall.tcwriter.pilot.GuiPilot {

	private final WebDriver driver;

	public SeleniumGuiPilot(final WebDriver driver) {
		this.driver = driver;
	}

	public WebDriver getDriver() {
		return driver;
	}

}
