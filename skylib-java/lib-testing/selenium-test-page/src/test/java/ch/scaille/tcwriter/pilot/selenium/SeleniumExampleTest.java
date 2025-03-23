package ch.scaille.tcwriter.pilot.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import ch.scaille.testing.testpilot.jupiter.DisabledIfHeadless;
import ch.scaille.testing.testpilot.selenium.SeleniumPilot;
import ch.scaille.testing.testpilot.selenium.WebDriverFactory;
import ch.scaille.util.helpers.Logs;

@ExtendWith(DisabledIfHeadless.class)
class SeleniumExampleTest extends AbstractSeleniumUndertowTest {

	@Override
	public WebDriver createWebDriver() {
		return WebDriverFactory.firefox().build();
	}

	/* **************************** TESTS **************************** */

	private SeleniumPilot pilot;

	@BeforeEach
	void createPilot() {
		pilot = new SeleniumPilot(driver);
	}

	@AfterEach
	void releasePilot() {
		try {
			Logs.of(this).info(pilot.getActionReport().getFormattedReport());
		} finally {
			pilot.close();
		}
	}

	@Test
	void testExample() throws SecurityException, InterruptedException {
		pilot.getDriver().get(localUrl.resolve("/example1.html").toString());

		final var mainPage = pilot.page(ExamplePage::new);

		mainPage.executeEnable();

		mainPage.expectTestAlertDialog();
		mainPage.testAlert();
		mainPage.assertDialogHandled();

		mainPage.clickOnMissingButton();

		mainPage.assertElementChange();

		 /*
		  * [[FirefoxDriver: firefox on linux (780b50ba-dc8c-484a-bde7-ca84325d50c4)] -> id: EnableTest]: clicked, Test delayed by: Wait until EnableTest enabled, [[FirefoxDriver: firefox on linux (780b50ba-dc8c-484a-bde7-ca84325d50c4)] -> id: AlertTest]: clicked, Acknowledging alert: Alert Test, visibility of element located by By.id: NotExisting: isSatisfied should have returned false, [[FirefoxDriver: firefox on linux (780b50ba-dc8c-484a-bde7-ca84325d50c4)] -> id: ElementChangeTest]: clicked]
		  */
		assertEquals(6, pilot.getActionReport().getReport().size(), () -> pilot.getActionReport().getFormattedReport());
	}

}
