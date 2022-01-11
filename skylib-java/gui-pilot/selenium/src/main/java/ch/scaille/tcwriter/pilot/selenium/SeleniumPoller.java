package ch.scaille.tcwriter.pilot.selenium;

import java.time.Duration;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.platform.commons.logging.LoggerFactory;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.scaille.util.helpers.Poller;
import io.netty.handler.logging.LogLevel;

public class SeleniumPoller extends Poller {

	private Logger LOGGER = Logger.getLogger(SeleniumPoller.class.getName());

	private WebDriver webDriver;
	private TimeoutException timeoutException;
	private Object lastResult;

	public SeleniumPoller(WebDriver webDriver, Duration timeout, Duration firstDelay, DelayFunction delayFunction) {
		super(timeout, firstDelay, delayFunction);
		this.webDriver = webDriver;
	}

	public <T> T run(Function<Poller, T> polling, Predicate<T> isSuccess,
			Function<TimeoutException, T> timeoutHandler) {
		timeoutException = null;
		T result = run(polling, isSuccess);
		if (timeoutException != null && lastResult != null) {
			return (T) lastResult;
		} else if (timeoutException != null) {
			timeoutHandler.apply(timeoutException);
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
			LOGGER.log(Level.INFO, "Polling timeout", e);
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
					lastResult = result;
					if (!isSuccess.test(result)) {
						return null;
					}
					return result;
				});

	}
}