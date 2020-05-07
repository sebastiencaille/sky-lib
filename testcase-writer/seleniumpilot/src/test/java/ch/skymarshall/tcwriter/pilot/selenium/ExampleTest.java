package ch.skymarshall.tcwriter.pilot.selenium;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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

public class ExampleTest {

	public static Undertow webServer = null;
	private static FirefoxDriver driver;

	@BeforeClass
	public static void startWebServer() {

		webServer = Undertow.builder().addHttpListener(8080, "localhost").setHandler(ExampleTest::handleExchange)
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

	// Not the best example..
	public static class AlertDelay implements ActionDelay {

		private final GuiPilot pilot;

		public AlertDelay(final GuiPilot pilot) {
			this.pilot = pilot;
		}

		@Override
		public boolean waitFinished() {
			new SeleniumAlertAction(pilot).acknowledge();
			return true;
		}

	}

	@Test
	public void testExample() {
		final GuiPilot pilot = new GuiPilot(driver);

		pilot.getDriver().get("http://localhost:8080/example1.html");
		new SeleniumAction(pilot, By.id("Bouh")).click().followedByDelay(new AlertDelay(pilot));
		new SeleniumAction(pilot, By.id("OK")).click();
	}

}
