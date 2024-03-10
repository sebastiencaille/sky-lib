package ch.scaille.tcwriter.pilot.selenium;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;

import ch.scaille.util.helpers.JavaExt;
import ch.scaille.util.helpers.Logs;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;

public abstract class AbstractTestWebAppProvider {

	/* **************************** WEB SERVER **************************** */

	protected static Undertow webServer = null;
	protected static WebDriver driver = null;

	public static final URL localUrl;

	static {
		try {
			localUrl = new URI(System.getProperty("url", "http://localhost:9999")).toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	public abstract WebDriver createWebDriver();

	public static void setDriver(WebDriver driver) {
		AbstractTestWebAppProvider.driver = driver;
	}

	@BeforeEach
	public void ensureWebDriverStarted() {
		if (driver == null) {
			setDriver(createWebDriver());
		}
	}

	@BeforeAll
	public static void startWebServer() {
		webServer = Undertow.builder()
				.addHttpListener(localUrl.getPort(), localUrl.getHost())
				.setHandler(AbstractTestWebAppProvider::handleWebExchange)
				.build();
		webServer.start();
	}

	public static void handleWebExchange(final HttpServerExchange exchange) {
		if ("/example1.html".equals(exchange.getRequestPath())) {

			exchange.getResponseHeaders().put(io.undertow.util.Headers.CONTENT_TYPE, "text/html");
			try (var in = Thread.currentThread()
					.getContextClassLoader()
					.getResourceAsStream("example/html/example1.html")) {
				exchange.getResponseSender().send(JavaExt.readUTF8Stream(in), StandardCharsets.UTF_8);
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
