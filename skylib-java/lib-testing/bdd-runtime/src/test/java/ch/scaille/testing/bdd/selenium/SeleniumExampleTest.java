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
import ch.scaille.util.helpers.Logs;

@ExtendWith(DisabledIfHeadless.class)
@SuppressWarnings("java:S2699")
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
		System.out.print(pilot.getActionReport().getFormattedReport());
		pilot.close();
	}

	@Test
	void testExample() {
		final var pageProvider = new AppPages(pilot);
		final var openPageScenario = AppSteps.BDD_FACTORY.scenario(AppSteps.OPEN_WEBSITE);
		final var testEnableScenario = openPageScenario.followedBy(AppSteps.TEST_ENABLE);
		final var testAlertScenario = testEnableScenario.followedBy(AppSteps.TEST_ALERT)
				.withConfigurer(p -> p.getContext().example = "Hello world");

		final var testEnableScenarioRun = testEnableScenario.run(pageProvider);
		final var testAlertScenarioRun = testAlertScenario.run(pageProvider);

		Logs.of(this).info(testEnableScenarioRun.toString());
		Logs.of(this).info(testAlertScenarioRun.toString());
	}

}
