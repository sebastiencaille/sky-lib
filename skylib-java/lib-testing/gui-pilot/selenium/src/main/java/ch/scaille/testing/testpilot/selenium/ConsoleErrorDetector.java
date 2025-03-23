package ch.scaille.testing.testpilot.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.log.FilterBy;
import org.openqa.selenium.bidi.log.LogLevel;
import org.openqa.selenium.bidi.module.LogInspector;
import org.openqa.selenium.bidi.module.Script;

import ch.scaille.util.helpers.Logs;

public class ConsoleErrorDetector {
	
	private static final String LAST_LOG = ConsoleErrorDetector.class.getSimpleName();
	
	private final LogInspector logInspector;
	private final List<String> errors = new ArrayList<>();
	private final WebDriver webDriver;
	private final Semaphore lastLogReceived = new Semaphore(0);
	
	public ConsoleErrorDetector(WebDriver webDriver, Pattern... ignoreRexeg) {
		this.webDriver = webDriver;
		this.logInspector = new LogInspector(webDriver);
		
		this.logInspector.onConsoleEntry(entry -> {
			for (Pattern pattern: ignoreRexeg) {
				if (pattern.matcher(entry.getText()).matches()) {
					return;
				}
			}
			errors.add(entry.getText());	
		}, FilterBy.logLevel(LogLevel.ERROR));
		
		this.logInspector.onConsoleEntry(entry -> {
			Logs.of(ConsoleErrorDetector.class).info(entry.getText());
			// We received the closing log
			if (LAST_LOG.equals(entry.getText())) {
				lastLogReceived.release();
			}
		});
	}
	
	public void assertNoError() {
		assertTrue(errors.isEmpty(), () -> "Errors must not be detected: " + String.join("\n", errors));
	}
	
	public void close() throws InterruptedException {
		try (var script = new Script(webDriver)) {
			script.evaluateFunctionInRealm(script.getAllRealms().get(0).getRealmId(), String.format("console.log('%s')", LAST_LOG), false, Optional.empty());
			assertTrue(lastLogReceived.tryAcquire(5, TimeUnit.SECONDS));
		}
		logInspector.close();
	}
	
	
}
