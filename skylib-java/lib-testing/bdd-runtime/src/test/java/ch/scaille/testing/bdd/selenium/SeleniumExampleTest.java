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
import ch.scaille.testing.bdd.definition.Story;
import ch.scaille.testing.bdd.definition.Story.StoryContext;
import ch.scaille.util.helpers.Log;

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
		StoryContext result = new Story<>(ExamplePage.OPEN_WEBSITE, ExamplePage.TEST_ENABLE, ExamplePage.TEST_ALERT)
				.run(pilot);
		Log.of(this).info(pilot.getActionReport().getFormattedReport());
		Log.of(this).info(result.toString());

	}

}
