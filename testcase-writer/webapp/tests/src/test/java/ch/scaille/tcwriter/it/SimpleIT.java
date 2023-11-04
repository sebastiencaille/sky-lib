package ch.scaille.tcwriter.it;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import ch.scaille.tcwriter.pilot.selenium.SeleniumPilot;
import ch.scaille.tcwriter.pilot.selenium.WebDriverFactory;
import ch.scaille.util.helpers.Logs;

class SimpleIT {

	public static WebDriver driver = WebDriverFactory.firefox().build();

	private SeleniumPilot pilot;

	@BeforeEach
	public void createPilot() {
		pilot = new SeleniumPilot(driver);
	}

	@AfterEach
	public void releasePilot() {
		Logs.of(this).info(pilot.getActionReport().getFormattedReport());
		pilot.close();
	}

	@AfterAll
	static void closeDriver() {
		driver.close();
	}

	@Test
	void simpleTest() throws MalformedURLException {
		pilot.getDriver().get(new URL("http://localhost:9000/index.html").toString());

		final var mainPage = pilot.page(MainPage::new);
		mainPage.select(MainPage.dictionary("Test dictionary"));
	}

}
