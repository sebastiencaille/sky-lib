package ch.scaille.testing.testpilot.selenium;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;

import ch.scaille.util.helpers.DelayFunction;
import lombok.extern.java.Log;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.opentest4j.AssertionFailedError;

import ch.scaille.util.helpers.Poller;

@Log
public class SeleniumPoller extends Poller {

	private final WebDriver webDriver;
	@Nullable
	private TimeoutException timeoutException;
	@Nullable
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
			log.fine("Timeout with polling result");
			return Optional.of((T) lastPollingResult);
		} else if (timeoutException != null) {
			log.fine("Timeout");
			return Optional.of(timeoutHandler.apply(timeoutException));
		}
		log.fine(() -> "Returning " + result);
		return result;
	}

	@Override
	public <T> Optional<T> run(Function<Poller, Optional<T>> polling, Predicate<T> isSuccess) {
		try {
			beforeRun();
			return pollWithSpecificDelay(polling, isSuccess, delayFunction.apply(this));
		} catch (TimeoutException e) {
			log.log(Level.INFO, "Polling timeout", e);
			timeoutException = e;
			return Optional.empty();
		}
	}

	private <T> Optional<T> pollWithSpecificDelay(Function<Poller, Optional<T>> polling, Predicate<T> isSuccess,
			Duration duration) {
		return Optional.of(new WebDriverWait(webDriver, timeTracker.remainingDuration()) //
				.withMessage(() -> {
					if (lastPollingResult != null) {
						return lastPollingResult.toString();
					}
					return "";
				})
				.pollingEvery(duration) //
				.ignoreAll(List.of(NoSuchElementException.class, StaleElementReferenceException.class,
						ElementNotInteractableException.class, UnhandledAlertException.class, AssertionFailedError.class,
						IndexOutOfBoundsException.class, TimeoutException.class))
				.until(_ -> {
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