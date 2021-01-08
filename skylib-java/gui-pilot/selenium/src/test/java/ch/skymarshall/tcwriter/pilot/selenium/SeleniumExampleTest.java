package ch.skymarshall.tcwriter.pilot.selenium;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.UnexpectedAlertBehaviour;
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

public class SeleniumExampleTest {

	/* **************************** WEB SERVER **************************** */

	public static Undertow webServer = null;
	private static FirefoxDriver driver;

	@BeforeClass
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

	@AfterClass
	public static void stopWebServer() {
		driver.quit();
		webServer.stop();
		webServer = null;
	}

	/* **************************** TESTS **************************** */

	private static final By PROCEED_LOCATION = By.id("Proceed");
	private static final By OK_LOCATION = By.id("OK");

	public static class ArbitraryDelay implements ActionDelay {

		private final SeleniumGuiPilot pilot;

		public ArbitraryDelay(final SeleniumGuiPilot pilot) {
			this.pilot = pilot;
		}

		@Override
		public boolean waitFinished() {
			pilot.element(PROCEED_LOCATION).wait(SeleniumElement.isEnabled());
			return true;
		}

	}

	private SeleniumGuiPilot pilot;

	@Before
	public void createPilot() {
		pilot = new SeleniumGuiPilot(driver);
	}

	@After
	public void releasePilot() {
		pilot.close();
	}

	@Test
	public void testExample() {

		pilot.getDriver().get("http://localhost:8080/example1.html");
		//
		pilot.element(PROCEED_LOCATION).wait(SeleniumElement.doClick().followedBy(new ArbitraryDelay(pilot)));
		pilot.expectModalDialog(s -> {
			s.doAcknowledge();
			return ModalDialogDetector.ignore();
		});
		pilot.element(OK_LOCATION).click();
		pilot.waitModalDialogHandled();

		pilot.element(By.id("NotExisting")).doIfEnabled(SeleniumElement.doClick(), Duration.ofMillis(500));

		Log.of(this).info(pilot.getActionReport().getFormattedReport());

		assertEquals(pilot.getActionReport().getFormattedReport(), 5, pilot.getActionReport().getReport().size());
	}

}
