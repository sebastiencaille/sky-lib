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
import ch.scaille.testing.bdd.definition.ScenarioFragment;
import ch.scaille.testing.bdd.definition.Story;
import ch.scaille.testing.bdd.definition.Story.ScenarioFactory;
import ch.scaille.testing.bdd.definition.Story.StoryContext;
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

	private static final ScenarioFactory<SeleniumPilot, PageProvider> BDD_FACTORY = Story.of(PageProvider::new);

	public static final ScenarioFragment<SeleniumPilot, PageProvider> OPEN_WEBSITE = BDD_FACTORY.with(
			step("I open the website", p -> p.driver.get("http://localhost:8080/example1.html")),
			step("I see that the website is open", p -> p.examplePage.testEnabled()));

	public static final ScenarioFragment<SeleniumPilot, PageProvider> TEST_ENABLE = BDD_FACTORY.with(
			step("I test the Enable function", p -> p.examplePage.testEnable()),
			step("I see that the Enable function is back to normal after some seconds",
					p -> p.examplePage.testEnabled()));

	public static final ScenarioFragment<SeleniumPilot, PageProvider> TEST_ALERT = BDD_FACTORY.with(
			step("I expect the Alert", p -> p.examplePage.expectTestAlertDialog()),
			step("I test the Alert function", p -> p.examplePage.testAlert()),
			step("I see that the Alert was raised|I acknowledge the Alert", p -> p.examplePage.checkDialogHandled()));

	@Test
	void testExample() {
		Story<SeleniumPilot, PageProvider> openPageStory = new Story<>(OPEN_WEBSITE);
		Story<SeleniumPilot, PageProvider> testEnableStory = openPageStory.followedBy(TEST_ENABLE);
		Story<SeleniumPilot, PageProvider> testAlertStory = testEnableStory.followedBy(TEST_ALERT);

		StoryContext testEnableStoryRun = testEnableStory.run(pilot);
		StoryContext testAlertStoryRun = testAlertStory.run(pilot);

		Logs.of(this).info(pilot.getActionReport().getFormattedReport());
		Logs.of(this).info(testEnableStoryRun.toString());
		Logs.of(this).info(testAlertStoryRun.toString());

	}

}
