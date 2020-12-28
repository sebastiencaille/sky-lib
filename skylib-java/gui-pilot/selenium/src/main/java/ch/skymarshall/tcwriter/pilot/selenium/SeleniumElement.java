package ch.skymarshall.tcwriter.pilot.selenium;

import static ch.skymarshall.tcwriter.pilot.PollingResult.onException;
import static ch.skymarshall.tcwriter.pilot.PollingResult.value;

import java.time.Duration;
import java.util.Arrays;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.skymarshall.tcwriter.pilot.AbstractGuiComponent;
import ch.skymarshall.tcwriter.pilot.EditionPolling;
import ch.skymarshall.tcwriter.pilot.Polling;
import ch.skymarshall.tcwriter.pilot.PollingResult;
import ch.skymarshall.tcwriter.pilot.StatePolling;

public class SeleniumElement extends AbstractGuiComponent<SeleniumElement, WebElement> {

	private static final Duration SHORT_POLLING_TIMEOUT = Duration.ofSeconds(5);
	private final SeleniumGuiPilot pilot;
	private final By locator;

	public SeleniumElement(final SeleniumGuiPilot pilot, final By locator) {
		super(pilot);
		this.pilot = pilot;
		this.locator = locator;
	}

	@Override
	public String toString() {
		return locator.toString();
	}

	@Override
	protected WebElement loadGuiComponent() {
		return pilot.getDriver().findElement(locator);
	}

	@Override
	protected boolean canCheck(final WebElement element) {
		return element.isDisplayed();
	}

	@Override
	protected boolean canEdit(final WebElement element) {
		return element.isDisplayed() && element.isEnabled();
	}

	/**
	 * Wait until the action is processed. First try with a short polling, then with
	 * a longer one
	 */
	@Override
	protected <U> PollingResult<WebElement, U> waitPollingSuccessLoop(final Polling<WebElement, U> polling,
			final Duration timeout) {
		Duration remains = timeout;
		if (timeout.compareTo(SHORT_POLLING_TIMEOUT) > 0) {
			// first use short polling time
			final PollingResult<WebElement, U> result = executeOnePolling(polling, SHORT_POLLING_TIMEOUT);
			if (result.isSuccess()) {
				return result;
			}
			remains = remains.minus(SHORT_POLLING_TIMEOUT);
		}
		return executeOnePolling(polling, remains);
	}

	/**
	 * Process a single wait
	 *
	 * @param <U>
	 * @param precondition
	 * @param applier
	 * @param timeout
	 * @return
	 */
	private <U> PollingResult<WebElement, U> executeOnePolling(final Polling<WebElement, U> polling,
			final Duration timeout) {
		try {
			return value(new WebDriverWait(pilot.getDriver(), timeout.getSeconds()) //
					.withTimeout(timeout) // set timeout in ms
					.pollingEvery(pollingTime(timeout)) //
					.ignoreAll(Arrays.asList(NoSuchElementException.class, StaleElementReferenceException.class,
							ElementNotInteractableException.class, UnhandledAlertException.class))
					.until(d -> {
						try {
							return executePolling(polling).orElse(null);
						} catch (final StaleElementReferenceException stale) {
							invalidateCache();
							throw stale;
						}
					}));
		} catch (final TimeoutException e) {
			return onException(e);
		}
	}

	public static Polling<WebElement, Boolean> isEnabled() {
		return StatePolling.<WebElement>satisfies(c -> true).withName("enabled");
	}

	public static Polling<WebElement, Boolean> doClick() {
		return EditionPolling.action(WebElement::click).withName("click");
	}

	public boolean doIfEnabled(final Polling<WebElement, Boolean> polling, final Duration shortTimeout) {
		return waitPollingSuccess(polling, shortTimeout, PollingResult.reportFailure("Not found: " + locator));
	}

	public void click() {
		wait(doClick());
	}

}
