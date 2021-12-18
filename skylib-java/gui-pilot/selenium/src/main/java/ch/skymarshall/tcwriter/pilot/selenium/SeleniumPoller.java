package ch.skymarshall.tcwriter.pilot.selenium;

import java.time.Duration;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.skymarshall.util.helpers.Poller;

public class SeleniumPoller extends Poller {

	private WebDriver webDriver;
	private TimeoutException timeoutException;

	public SeleniumPoller(WebDriver webDriver, Duration timeout, Duration firstDelay, DelayFunction delayFunction) {
		super(timeout, firstDelay, delayFunction);
		this.webDriver = webDriver;
	}

	public <T> T run(Function<Poller, T> polling, Predicate<T> isSuccess,
			Function<TimeoutException, T> timeoutHandler) {
		timeoutException = null;
		T result = run(polling, isSuccess);
		if (timeoutException != null) {
			return timeoutHandler.apply(timeoutException);
		}
		return result;
	}

	@Override
	public <T> T run(Function<Poller, T> polling, Predicate<T> isSuccess) {
		try {
			beforeRun();
			T result = pollWithSpecificDelay(polling, isSuccess, firstDelay);
			if (isSuccess.test(result)) {
				return result;
			}
			return pollWithSpecificDelay(polling, isSuccess, delayFunction.apply(this));
		} catch (TimeoutException e) {
			timeoutException = e;
			return null;
		}
	}

	private <T> T pollWithSpecificDelay(Function<Poller, T> polling, Predicate<T> isSuccess, Duration duration) {
		return new WebDriverWait(webDriver, timeTracker.remainingDuration()) //
				.pollingEvery(duration) //
				.ignoreAll(Arrays.asList(NoSuchElementException.class, StaleElementReferenceException.class,
						ElementNotInteractableException.class, UnhandledAlertException.class))
				.<T>until(d -> {
					T result = polling.apply(this);
					if (!isSuccess.test(result)) {
						return null;
					}
					return result;
				});

	}
}