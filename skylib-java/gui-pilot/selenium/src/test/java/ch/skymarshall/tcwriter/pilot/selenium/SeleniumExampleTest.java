package ch.skymarshall.tcwriter.pilot.selenium;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.xnio.streams.Streams;

import ch.skymarshall.tcwriter.pilot.ActionDelay;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class SeleniumExampleTest {

	/* **************************** WEB SERVER **************************** */

	public static Undertow webServer = null;
	private static FirefoxDriver driver;

	@BeforeClass
	public static void startWebServer() {

		webServer = Undertow.builder().addHttpListener(8080, "localhost").setHandler(SeleniumExampleTest::handleExchange)
				.build();
		webServer.start();

		// Start selenium
		System.setProperty("webdriver.gecko.driver", "/usr/local/bin/geckodriver");

		final FirefoxBinary firefoxBinary = new FirefoxBinary();
		firefoxBinary.addCommandLineOptions("--no-sandbox");

		final FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary);

		driver = new FirefoxDriver(firefoxOptions);
	}

	public static void handleExchange(final HttpServerExchange exchange) {
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
			new SeleniumElement(pilot, PROCEED_LOCATION).waitEnabled();
			return true;
		}

	}

	@Test
	public void testExample() {
		final SeleniumGuiPilot pilot = new SeleniumGuiPilot(driver);

		pilot.getDriver().get("http://localhost:8080/example1.html");
		//
		new SeleniumElement(pilot, PROCEED_LOCATION).click().followedByDelay(new ArbitraryDelay(pilot));
		new SeleniumElement(pilot, OK_LOCATION).click();
		new SeleniumAlert(pilot).acknowledge();

		new SeleniumElement(pilot, By.id("NotExisting")).clickIfEnabled(Duration.ofMillis(500));

		System.out.println(pilot.getActionReport().getFormattedReport());
		assertEquals(pilot.getActionReport().getFormattedReport(), 5, pilot.getActionReport().getReport().size());
	}

}
