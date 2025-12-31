package ch.scaille.tcwriter.pilot.selenium;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.remote.RemoteWebDriver;

import ch.scaille.testing.testpilot.selenium.ConsoleErrorDetector;
import ch.scaille.util.helpers.JavaExt;
import ch.scaille.util.helpers.Logs;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;

@NullMarked
public abstract class AbstractSeleniumUndertowTest {

	@Nullable
	private ConsoleErrorDetector consoleErrorDetector;
	
	/* **************************** WEB SERVER **************************** */

	@Nullable
	protected static Undertow webServer = null;
	@Nullable
	protected static RemoteWebDriver driver = null;

	public static final URI localUrl;

	static {
		try {
			localUrl = new URI(System.getProperty("url", "http://localhost:9999"));
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	public abstract RemoteWebDriver createWebDriver();

	public static void setDriver(RemoteWebDriver driver) {
		AbstractSeleniumUndertowTest.driver = driver;
	}

	@BeforeEach
	public void ensureWebDriverStarted() {
		if (driver == null) {
			setDriver(createWebDriver());
		}
		consoleErrorDetector = new ConsoleErrorDetector(driver);
	}

	@AfterEach
	public void checkLogs() throws InterruptedException {
		if (consoleErrorDetector != null) {
			consoleErrorDetector.close();
			consoleErrorDetector.assertNoError();
			consoleErrorDetector = null;
		}
	}
	
	@BeforeAll
	public static void startWebServer() {
		webServer = Undertow.builder()
				.addHttpListener(localUrl.getPort(), localUrl.getHost())
				.setHandler(AbstractSeleniumUndertowTest::handleWebExchange)
				.build();
		webServer.start();
	}

	public static void handleWebExchange(final HttpServerExchange exchange) {
		if ("/example1.html".equals(exchange.getRequestPath())) {

			exchange.getResponseHeaders().put(io.undertow.util.Headers.CONTENT_TYPE, "text/html");
			try (var in = Thread.currentThread()
					.getContextClassLoader()
					.getResourceAsStream("example/html/example1.html")) {
				exchange.getResponseSender().send(JavaExt.readUTF8Stream(Objects.requireNonNull(in, "Resource was not found")), StandardCharsets.UTF_8);
			} catch (final IOException e) {
				throw new IllegalStateException("Cannot serve file", e);
			}
		}
	}

	@AfterAll
	public static void stopWebServer() {
		if (driver != null) {
			driver.quit();
		}
		if (webServer != null) {
			webServer.stop();
			webServer = null;
		}
	}

	/* **************************** TESTS **************************** */

	@BeforeAll
	public static void initLogger() {
		final var rootLogger = Logs.of("ch");
		rootLogger.setLevel(Level.ALL);
		final var console = new ConsoleHandler();
		console.setLevel(Level.ALL);
		rootLogger.addHandler(console);
	}

}
