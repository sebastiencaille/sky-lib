package ch.scaille.tcwriter.it;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import ch.scaille.testing.testpilot.jupiter.DisabledIfHeadless;
import ch.scaille.testing.testpilot.selenium.SeleniumPilot;
import ch.scaille.testing.testpilot.selenium.WebDriverFactory;
import ch.scaille.testing.testpilot.selenium.jupiter.ScreenShotExtensions;
import ch.scaille.testing.testpilot.selenium.jupiter.WebDriverExtension;
import ch.scaille.testing.testpilot.selenium.jupiter.WebDriverExtension.WebDriverConfigurer;
import ch.scaille.util.helpers.Logs;
import org.openqa.selenium.WebDriver;

@ExtendWith({ DisabledIfHeadless.class, WebDriverExtension.class, ScreenShotExtensions.class })
class SimpleIT {

	private SeleniumPilot pilot;

	@BeforeEach
	void createPilot(WebDriverConfigurer webDriverHolder) throws URISyntaxException, MalformedURLException  {
		final var webDriver = webDriverHolder.getOrCreate(() -> WebDriverFactory.firefox().build());
		final var port = System.getProperty("app.port", "5173");
		
		pilot = new SeleniumPilot(webDriver);
		pilot.getDriver().get(new URI("http://localhost:" + port + "/index.html").toURL().toString());
	}

	@AfterEach
	void releasePilot() {
		Logs.of(this).info(pilot.getActionReport().getFormattedReport());
		pilot.close();
	}

	@AfterAll
	static void closeDriver(WebDriverConfigurer webDriverHolder) {
		webDriverHolder.getDriver().ifPresent(WebDriver::quit);
	}
	
	@Test
	void simpleTest() {
		final var mainPage = pilot.page(MainPage::new);
		
		mainPage.assertAvailable(MainPage.testDictionary());
		mainPage.select(MainPage.testDictionary());
		
		mainPage.assertAvailable(MainPage.currentTestCase());
		mainPage.select(MainPage.currentTestCase());
	}

}
