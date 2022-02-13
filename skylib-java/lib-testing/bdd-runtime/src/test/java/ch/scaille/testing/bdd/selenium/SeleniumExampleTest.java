package ch.scaille.testing.bdd.selenium;

import static ch.scaille.testing.bdd.definition.ScenarioFragment.step;

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
import ch.scaille.testing.bdd.definition.ScenarioFragment;
import ch.scaille.testing.bdd.definition.TestDictionary;
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

	/** BDD **/

	private static final TestDictionary<PageProvider> BDD_FACTORY = new TestDictionary<>();

	public static final ScenarioFragment<PageProvider> OPEN_WEBSITE = BDD_FACTORY.with(
			step("I open the website", p -> p.driver.get("http://localhost:8080/example1.html")),
			step("I see that the website is open", p -> p.examplePage.testEnabled()));

	public static final ScenarioFragment<PageProvider> TEST_ENABLE = BDD_FACTORY.with(
			step("I execute the Enable function", p -> p.examplePage.testEnable()),
			step("I see that the Enable function is disabled|And back to normal after some seconds",
					p -> p.examplePage.testEnabled()));

	public static final ScenarioFragment<PageProvider> TEST_ALERT = BDD_FACTORY.with(
			step("I expect the Alert", p -> p.examplePage.expectTestAlertDialog()),
			step("I test the Alert function", p -> p.examplePage.testAlert()),
			step("I see that the Alert was raised|I acknowledge the Alert", p -> p.examplePage.checkDialogHandled()));

	@Test
	void testExample() {
		PageProvider pageProvider = new PageProvider(pilot);
		Scenario<PageProvider> openPageScenario = BDD_FACTORY.scenario(OPEN_WEBSITE);
		Scenario<PageProvider> testEnableScenario = openPageScenario.followedBy(TEST_ENABLE);
		Scenario<PageProvider> testAlertScenario = testEnableScenario.followedBy(TEST_ALERT);

		ExecutionContext testEnableScenarioRun = testEnableScenario.run(pageProvider);
		ExecutionContext testAlertScenarioRun = testAlertScenario.run(pageProvider);

		Logs.of(this).info(testEnableScenarioRun.toString());
		Logs.of(this).info(testAlertScenarioRun.toString());

	}

}
