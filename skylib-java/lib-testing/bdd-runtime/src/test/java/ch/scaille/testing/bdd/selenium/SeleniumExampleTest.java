package ch.scaille.testing.bdd.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import ch.scaille.tcwriter.jupiter.DisabledIfHeadless;
import ch.scaille.tcwriter.pilot.selenium.AbstractTestWebAppProvider;
import ch.scaille.tcwriter.pilot.selenium.SeleniumPilot;
import ch.scaille.tcwriter.pilot.selenium.WebDriverFactory;
import ch.scaille.testing.bdd.definition.Scenario;
import ch.scaille.testing.bdd.definition.Scenario.ExecutionContext;
import ch.scaille.util.helpers.Logs;

@ExtendWith(DisabledIfHeadless.class)
class SeleniumExampleTest extends AbstractTestWebAppProvider {

	@Override
	public WebDriver createWebDriver() {
		if (WebDriverFactory.IS_WINDOWS) {
			return WebDriverFactory.firefox("C:\\selenium\\drivers\\geckodriver_win32\\geckodriver.exe").build();
		} else {
			return WebDriverFactory.firefox("/usr/bin/geckodriver").build();
		}
	}

	/* **************************** TESTS **************************** */

	private SeleniumPilot pilot;

	@BeforeEach
	public void createPilot() {
		pilot = new SeleniumPilot(driver);
	}

	@AfterEach
	public void releasePilot() {
		System.out.print(pilot.getActionReport().getFormattedReport());
		pilot.close();
	}

	@Test
	void testExample() {
		AppPages pageProvider = new AppPages(pilot);
		Scenario<AppPages> openPageScenario = AppSteps.BDD_FACTORY.scenario(AppSteps.OPEN_WEBSITE);
		Scenario<AppPages> testEnableScenario = openPageScenario.followedBy(AppSteps.TEST_ENABLE);
		Scenario<AppPages> testAlertScenario = testEnableScenario.followedBy(AppSteps.TEST_ALERT);

		ExecutionContext testEnableScenarioRun = testEnableScenario.run(pageProvider);
		ExecutionContext testAlertScenarioRun = testAlertScenario.run(pageProvider);

		Logs.of(this).info(testEnableScenarioRun.toString());
		Logs.of(this).info(testAlertScenarioRun.toString());

	}

}
