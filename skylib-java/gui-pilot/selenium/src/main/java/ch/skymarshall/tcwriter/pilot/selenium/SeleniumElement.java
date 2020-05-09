package ch.skymarshall.tcwriter.pilot.selenium;

import static ch.skymarshall.tcwriter.pilot.Polling.action;
import static ch.skymarshall.tcwriter.pilot.Polling.isTrue;
import static ch.skymarshall.tcwriter.pilot.Polling.onException;
import static ch.skymarshall.tcwriter.pilot.Polling.throwError;
import static ch.skymarshall.tcwriter.pilot.Polling.value;

import java.time.Duration;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.skymarshall.tcwriter.pilot.AbstractGuiComponent;
import ch.skymarshall.tcwriter.pilot.Polling;

public class SeleniumElement extends AbstractGuiComponent<WebElement, SeleniumElement> {

	private static final Duration QUICK_TIMEOUT = Duration.ofSeconds(5);
	private final GuiPilot pilot;
	private final By locator;

	protected static boolean canInteract(final WebElement element) {
		return element.isDisplayed() && element.isEnabled();
	}

	public SeleniumElement(final GuiPilot pilot, final By locator) {
		super(pilot);
		this.pilot = pilot;
		this.locator = locator;
	}

	@Override
	public String toString() {
		return locator.toString();
	}

	@Override
	protected WebElement loadElement() {
		return pilot.getDriver().findElement(locator);
	}

	/**
	 * Wait until the action is processed. First try with a short polling, then with
	 * a longer one
	 */
	@Override
	protected <U> Polling<WebElement, U> waitActionSuccessLoop(final Predicate<WebElement> precondition,
			final Function<WebElement, Polling<WebElement, U>> applier, final Duration timeout) {
		Duration remains = timeout;
		if (timeout.compareTo(QUICK_TIMEOUT) > 0) {
			// first do quick polling
			final Polling<WebElement, U> result = executeOnePolling(precondition, applier, QUICK_TIMEOUT);
			if (result.success()) {
				return result;
			}
			remains = remains.minus(QUICK_TIMEOUT);
		}
		return executeOnePolling(precondition, applier, remains);
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
	private <U> Polling<WebElement, U> executeOnePolling(final Predicate<WebElement> precondition,
			final Function<WebElement, Polling<WebElement, U>> applier, final Duration timeout) {
		try {
			return value(new WebDriverWait(pilot.getDriver(), timeout.getSeconds()) //
					.withTimeout(timeout) // set timeout in ms
					.pollingEvery(pollingTime(timeout)) //
					.ignoreAll(Arrays.asList(NoSuchElementException.class, StaleElementReferenceException.class,
							ElementNotInteractableException.class))
					.<U>until(d -> {
						try {
							return executePolling(precondition, applier).orElse(null);
						} catch (final StaleElementReferenceException stale) {
							invalidateCache();
							throw stale;
						}
					}));
		} catch (final TimeoutException e) {
			return onException(e);
		}
	}

	protected <U> U executeInteractiveAction(final Function<WebElement, Polling<WebElement, U>> applier) {
		return waitActionSuccess(SeleniumElement::canInteract, applier, pilot.getDefaultActionTimeout(), throwError());
	}

	public SeleniumElement waitEnabled() {
		withReport(e -> "wait enabled").executeInteractiveAction(e -> isTrue());
		return this;
	}

	public SeleniumElement click() {
		withReport(e -> "click").executeInteractiveAction(action(WebElement::click));
		return this;
	}

	public boolean clickIfEnabled(final Duration shortTimeout) {
		return withReport(e -> "click if enabled").waitActionSuccess(SeleniumElement::canInteract,
				action(WebElement::click), shortTimeout, Polling.report("Not found: " + locator));

	}

}
