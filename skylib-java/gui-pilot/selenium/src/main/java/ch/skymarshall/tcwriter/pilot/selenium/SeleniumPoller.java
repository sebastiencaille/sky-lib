package ch.skymarshall.tcwriter.pilot.selenium;

import java.time.Duration;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.skymarshall.util.helpers.Poller;

public class SeleniumPoller extends Poller {

	private WebDriver webDriver;

	public SeleniumPoller(WebDriver webDriver, Duration timeout, Duration firstDelay, DelayFunction delayFunction) {
		super(timeout, firstDelay, delayFunction);
		this.webDriver = webDriver;
	}

	@Override
	public <T, E extends Exception> T run(Supplier<T> polling, Predicate<T> isSuccess) throws TimeoutException {
		beforeRun();
		T result = pollWithSpecificDelay(polling, isSuccess, firstDelay);
		if (isSuccess.test(result)) {
			return result;
		}
		return pollWithSpecificDelay(polling, isSuccess, delayFunction.apply(this));
	}

	private <T> T pollWithSpecificDelay(Supplier<T> polling, Predicate<T> isSuccess, Duration duration) throws TimeoutException {

		return new WebDriverWait(webDriver, timeTracker.remainingDuration()) //
				.pollingEvery(duration) //
				.ignoreAll(Arrays.asList(NoSuchElementException.class, StaleElementReferenceException.class,
						ElementNotInteractableException.class, UnhandledAlertException.class))
				.<T>until(d -> {
					T result = polling.get();
					if (!isSuccess.test(result)) {
						return null;
					}
					return result;
				});

	}
}