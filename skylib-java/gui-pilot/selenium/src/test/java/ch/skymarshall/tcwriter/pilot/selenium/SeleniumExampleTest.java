package ch.skymarshall.tcwriter.pilot.selenium;

import static ch.skymarshall.tcwriter.pilot.EditionPolling.action;
import static ch.skymarshall.tcwriter.pilot.selenium.ElementPilot.click;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.xnio.streams.Streams;

import ch.skymarshall.tcwriter.pilot.ActionDelay;
import ch.skymarshall.tcwriter.pilot.ModalDialogDetector;
import ch.skymarshall.util.helpers.Log;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

class SeleniumExampleTest {

	/* **************************** WEB SERVER **************************** */

	public static Undertow webServer = null;
	private static FirefoxDriver driver;

	@BeforeAll
	public static void startWebServer() {

		webServer = Undertow.builder().addHttpListener(8080, "localhost")
				.setHandler(SeleniumExampleTest::handleWebExchange).build();
		webServer.start();

		// Start selenium
		System.setProperty("webdriver.gecko.driver", "/usr/bin/geckodriver");

		final FirefoxBinary firefoxBinary = new FirefoxBinary();
		firefoxBinary.addCommandLineOptions("--no-sandbox");

		final FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary);
		firefoxOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);

		driver = new FirefoxDriver(firefoxOptions);
	}

	public static void handleWebExchange(final HttpServerExchange exchange) {
		if ("/example1.html".equals(exchange.getRequestPath())) {

			exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
			try (InputStream res = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("example/html/example1.html")) {
				final ByteArrayOutputStream bout = new ByteArrayOutputStream();
				Streams.copyStream(res, bout);
				exchange.getResponseSender().send(new String(bout.toByteArray(), StandardCharsets.UTF_8),
						StandardCharsets.UTF_8);
			} catch (final IOException e) {
				throw new IllegalStateException("Cannot serve file", e);
			}
		}
	}

	@AfterAll
	public static void stopWebServer() {
		driver.quit();
		webServer.stop();
		webServer = null;
	}

	/* **************************** TESTS **************************** */

	public static class ProceedEnabledDelay implements ActionDelay {

		private PagePilot<ExamplePage> mainPage;

		public ProceedEnabledDelay(final PagePilot<ExamplePage> mainPage) {
			this.mainPage = mainPage;
		}

		@Override
		public boolean waitFinished() {
			mainPage.wait(p -> p.proceed, ElementPilot.isEnabled());
			Assertions.assertTrue(mainPage.page().proceed.isEnabled(), () -> "Proceed is enabled");
			return true;
		}
		
		@Override
		public String toString() {
			return "Wait on Proceed enabled";
		}

	}

	private SeleniumGuiPilot pilot;

	@BeforeAll
	public static void initLogger() {
		Logger rootLogger = Logger.getLogger("ch");
		rootLogger.setLevel(Level.ALL);
		ConsoleHandler console = new ConsoleHandler();
		console.setLevel(Level.ALL);
		rootLogger.addHandler(console);
	}

	@BeforeEach
	public void createPilot() {
		pilot = new SeleniumGuiPilot(driver);
	}

	@AfterEach
	public void releasePilot() {
		System.out.print(pilot.getActionReport().getFormattedReport());
		pilot.close();
	}

	@Test
	void testExample() {

		pilot.getDriver().get("http://localhost:8080/example1.html");
		//
		PagePilot<ExamplePage> mainPage = pilot.page(ExamplePage.class);

		// Perform a click, and tell the next action that the next action must wait
		// until "Proceed" is enabled
		mainPage.wait(p -> p.proceed, click().followedBy(new ProceedEnabledDelay(mainPage)));

		// Handle the modal dialog raised by the click
		pilot.expectModalDialog(s -> {
			s.doAcknowledge();
			return ModalDialogDetector.expected();
		});

		// click on ok
		// mainPage.element(p -> p.ok).wait(WebElement::click);
		// mainPage.element(p -> p.ok).wait(click());
		// mainPage.element(p -> p.ok).wait(action(WebElement::click));
		mainPage.wait(p -> p.ok, WebElement::click);
		pilot.waitModalDialogHandled();

		mainPage.ifEnabled(p -> p.notExisting, action(WebElement::click), Duration.ofMillis(500));

		Log.of(this).info(pilot.getActionReport().getFormattedReport());

		assertEquals(6, pilot.getActionReport().getReport().size(), () -> pilot.getActionReport().getFormattedReport());
	}

}
