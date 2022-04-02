package ch.scaille.tcwriter.pilot.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import ch.scaille.tcwriter.jupiter.DisabledIfHeadless;
import ch.scaille.util.helpers.Logs;

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
	void testExample() {

		pilot.getDriver().get("http://localhost:8080/example1.html");

		ExamplePage mainPage = pilot.page(ExamplePage::new);

		mainPage.testEnable();

		mainPage.expectTestAlertDialog();
		mainPage.testAlert();
		mainPage.checkDialogHandled();

		mainPage.clickOnMissingButton();

		mainPage.testElementChange();

		assertEquals(8, pilot.getActionReport().getReport().size(), () -> pilot.getActionReport().getFormattedReport());
	}

}
