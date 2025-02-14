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

	public static final WebDriver driver = WebDriverFactory.firefox().build();

	private SeleniumPilot pilot;

	@BeforeEach
	public void createPilot() throws URISyntaxException, MalformedURLException  {
		final var port = System.getProperty("app.port", "5173");
		
		pilot = new SeleniumPilot(driver);
		pilot.getDriver().get(new URI("http://localhost:" + port + "/index.html").toURL().toString());
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
	void simpleTest() {
		final var mainPage = pilot.page(MainPage::new);
		mainPage.select(MainPage.dictionary("Test dictionary"));
		mainPage.assertAvailable(MainPage.currentTestCase());
		mainPage.select(MainPage.currentTestCase());
	}

}
