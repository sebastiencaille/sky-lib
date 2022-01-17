package ch.scaille.tcwriter.pilot.selenium.bdd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.xnio.streams.Streams;

import ch.scaille.tcwriter.jupiter.DisabledIfHeadless;
import ch.scaille.tcwriter.pilot.selenium.SeleniumPilot;
import ch.scaille.tcwriter.pilot.selenium.WebDriverFactory;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

@ExtendWith(DisabledIfHeadless.class)
class SeleniumExampleTest {

	/* **************************** WEB SERVER **************************** */

	public static Undertow webServer = null;
	private static WebDriver driver;

	@BeforeAll
	public static void startWebServer() {

		webServer = Undertow.builder().addHttpListener(8080, "localhost")
				.setHandler(SeleniumExampleTest::handleWebExchange).build();
		webServer.start();

		driver = WebDriverFactory.firefox("/usr/bin/geckodriver").build();

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

	private SeleniumPilot pilot;

	@BeforeAll
	public static void initLogger() {
		Logger rootLogger = Logger.getLogger("ch");
		rootLogger.setLevel(Level.INFO);
		ConsoleHandler console = new ConsoleHandler();
		console.setLevel(Level.INFO);
		rootLogger.addHandler(console);
	}

	@BeforeEach
	public void createPilot() {
		pilot = new SeleniumPilot(driver);
	}

	@AfterEach
	public void releasePilot() {
		System.out.print(pilot.getActionReport().getFormattedReport());
		pilot.close();
	}


}
