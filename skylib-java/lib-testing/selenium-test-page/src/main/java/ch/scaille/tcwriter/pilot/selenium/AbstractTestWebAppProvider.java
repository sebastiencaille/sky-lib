package ch.scaille.tcwriter.pilot.selenium;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.xnio.streams.Streams;

import ch.scaille.util.helpers.Logs;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;

public abstract class AbstractTestWebAppProvider {

	/* **************************** WEB SERVER **************************** */

	protected static Undertow webServer = null;
	protected static WebDriver driver = null;

	public abstract WebDriver createWebDriver();

	@BeforeEach
	public void ensureWebDriverStarted() {
		if (driver == null) {
			driver = createWebDriver();
		}
	}

	@BeforeAll
	public static void startWebServer() {

		webServer = Undertow.builder().addHttpListener(8080, "localhost")
				.setHandler(AbstractTestWebAppProvider::handleWebExchange).build();
		webServer.start();

	}

	public static void handleWebExchange(final HttpServerExchange exchange) {
		if ("/example1.html".equals(exchange.getRequestPath())) {

			exchange.getResponseHeaders().put(io.undertow.util.Headers.CONTENT_TYPE, "text/html");
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

	@BeforeAll
	public static void initLogger() {
		Logger rootLogger = Logs.of("ch");
		rootLogger.setLevel(Level.ALL);
		ConsoleHandler console = new ConsoleHandler();
		console.setLevel(Level.ALL);
		rootLogger.addHandler(console);
	}

}
