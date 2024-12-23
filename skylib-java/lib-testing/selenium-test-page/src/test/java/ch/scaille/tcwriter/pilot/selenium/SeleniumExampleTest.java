package ch.scaille.tcwriter.pilot.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import ch.scaille.tcwriter.jupiter.DisabledIfHeadless;
import ch.scaille.tcwriter.pilot.AbstractEvent.EventWaiter;
import ch.scaille.tcwriter.pilot.selenium.BiDiEvent.BiDiEventConfig;
import ch.scaille.tcwriter.pilot.selenium.BiDiEvent.IBiDiEvent;
import ch.scaille.tcwriter.pilot.selenium.BiDiEvent.MutationConfig;
import ch.scaille.util.helpers.Logs;

@ExtendWith(DisabledIfHeadless.class)
class SeleniumExampleTest extends AbstractTestWebAppProvider {

	private static String TEXT_XPATH = "//div[@id='ElementChangeHolder']";
	
	private enum TestEvents implements IBiDiEvent {
	
		DISPLAY_HELLO(new BiDiEventConfig(TEXT_XPATH, MutationConfig.CHILD_LIST, false, "mutation => {console.log(mutation); return mutation.target.children[0].textContent === 'Hello' }")),
		DISPLAY_HELLO_AGAIN(new BiDiEventConfig(TEXT_XPATH, MutationConfig.CHILD_LIST, false, "mutation => mutation.target.children[0].textContent === 'Hello again'"));

		private final BiDiEventConfig biDiEventConfig;

		TestEvents(BiDiEventConfig biDiEventConfig) {
			this.biDiEventConfig = biDiEventConfig;
		}

		@Override
		public BiDiEventConfig config() {
			return biDiEventConfig;
		}
	}

	@Override
	public WebDriver createWebDriver() {
		return WebDriverFactory.firefox().build();
	}

	/* **************************** TESTS **************************** */

	private SeleniumPilot pilot;
	private BiDiEvent<TestEvents> helloEventListener;
	private ConsoleErrorDetector consoleErrorDetector;

	@BeforeEach
	public void createPilot() {
		pilot = new SeleniumPilot(driver);
		consoleErrorDetector = new ConsoleErrorDetector(driver);
		helloEventListener = new BiDiEvent<>(driver, TestEvents.DISPLAY_HELLO, TestEvents.DISPLAY_HELLO_AGAIN);
	}

	@AfterEach
	public void releasePilot() throws InterruptedException {
		try {
			Logs.of(this).info(pilot.getActionReport().getFormattedReport());
			consoleErrorDetector.assertNoError();
		} finally {
			pilot.close();
		}
	}

	@Test
	void testExample() throws SecurityException {
		pilot.getDriver().get(localUrl.resolve("/example1.html").toString());

		final var mainPage = pilot.page(ExamplePage::new);

		EventWaiter<TestEvents> eventsWaiter = helloEventListener.expect();
		mainPage.executeEnable();

		mainPage.expectTestAlertDialog();
		mainPage.testAlert();
		mainPage.assertDialogHandled();

		mainPage.clickOnMissingButton();

		mainPage.assertElementChange();

		assertTrue(eventsWaiter.matches());
		assertEquals(6, pilot.getActionReport().getReport().size(), () -> pilot.getActionReport().getFormattedReport());
	}

}
