package ch.scaille.tcwriter.it;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import ch.scaille.tcwriter.jupiter.DisabledIfHeadless;
import ch.scaille.tcwriter.pilot.selenium.SeleniumPilot;
import ch.scaille.tcwriter.pilot.selenium.WebDriverFactory;
import ch.scaille.util.helpers.Logs;

@ExtendWith(DisabledIfHeadless.class)
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
		driver.quit();
	}

	@Test
	void simpleTest() throws URISyntaxException, MalformedURLException {
		pilot.getDriver().get(new URI("http://localhost:9000/index.html").toURL().toString());

		final var mainPage = pilot.page(MainPage::new);
		mainPage.select(MainPage.dictionary("Test dictionary"));
		mainPage.select(MainPage.currentTestCase());
	}

}
