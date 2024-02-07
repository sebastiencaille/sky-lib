package ch.scaille.tcwriter.pilot.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import ch.scaille.tcwriter.jupiter.DisabledIfHeadless;
import ch.scaille.util.helpers.Logs;

import java.io.IOException;
import java.net.URL;

@ExtendWith(DisabledIfHeadless.class)
class SeleniumExampleTest extends AbstractTestWebAppProvider {

	@Override
	public WebDriver createWebDriver() {
		return WebDriverFactory.firefox().build();
	}

	/* **************************** TESTS **************************** */

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

	@Test
	void testExample() throws SecurityException, IOException {
		pilot.getDriver().get(new URL(localUrl, "example1.html").toString());

		final var mainPage = pilot.page(ExamplePage::new);

		mainPage.executeEnable();

		mainPage.expectTestAlertDialog();
		mainPage.testAlert();
		mainPage.assertDialogHandled();

		mainPage.clickOnMissingButton();

		mainPage.assertElementChange();

		assertEquals(8, pilot.getActionReport().getReport().size(), () -> pilot.getActionReport().getFormattedReport());
	}

}
