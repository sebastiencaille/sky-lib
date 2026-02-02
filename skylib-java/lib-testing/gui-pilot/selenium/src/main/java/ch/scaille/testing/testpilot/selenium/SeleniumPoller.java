package ch.scaille.testing.testpilot.selenium;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.scaille.util.helpers.DelayFunction;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.opentest4j.AssertionFailedError;

import ch.scaille.util.helpers.Logs;
import ch.scaille.util.helpers.Poller;

public class SeleniumPoller extends Poller {

	private static final Logger LOGGER = Logs.of(SeleniumPoller.class);

	private final WebDriver webDriver;
	private TimeoutException timeoutException;
	private Object lastPollingResult;

	public SeleniumPoller(WebDriver webDriver, Duration timeout, Duration firstDelay, DelayFunction delayFunction) {
		super(timeout, firstDelay, delayFunction);
		this.webDriver = webDriver;
	}

	public <T> Optional<T> run(Function<Poller, Optional<T>> polling, Predicate<T> isSuccess,
			Function<TimeoutException, T> timeoutHandler) {
		timeoutException = null;
		final var result = run(polling, isSuccess);
		if (timeoutException != null && lastPollingResult != null) {
			// Return the root cause of the failure, which is nicer than selenium exception
			LOGGER.fine("Timeout with polling result");
			return Optional.of((T) lastPollingResult);
		} else if (timeoutException != null) {
			LOGGER.fine("Timeout");
			return Optional.of(timeoutHandler.apply(timeoutException));
		}
		LOGGER.fine(() -> "Returning " + result);
		return result;
	}

	@Override
	public <T> Optional<T> run(Function<Poller, Optional<T>> polling, Predicate<T> isSuccess) {
		try {
			beforeRun();
			return pollWithSpecificDelay(polling, isSuccess, delayFunction.apply(this));
		} catch (TimeoutException e) {
			LOGGER.log(Level.INFO, "Polling timeout", e);
			timeoutException = e;
			return Optional.empty();
		}
	}

	private <T> Optional<T> pollWithSpecificDelay(Function<Poller, Optional<T>> polling, Predicate<T> isSuccess,
			Duration duration) {
		return Optional.ofNullable(new WebDriverWait(webDriver, timeTracker.remainingDuration()) //
				.withMessage(() -> {
					if (lastPollingResult != null) {
						return lastPollingResult.toString();
					}
					return null;
				})
				.pollingEvery(duration) //
				.ignoreAll(List.of(NoSuchElementException.class, StaleElementReferenceException.class,
						ElementNotInteractableException.class, UnhandledAlertException.class, AssertionFailedError.class,
						IndexOutOfBoundsException.class, TimeoutException.class))
				.until(d -> {
					try {
						final var result = polling.apply(this);
						lastPollingResult = result.orElse(null);
						return result.filter(isSuccess).orElse(null);
					} catch (InvalidSelectorException e) {
						throw new IllegalStateException("Selenium misuse", e);
					}
				}));

	}
}