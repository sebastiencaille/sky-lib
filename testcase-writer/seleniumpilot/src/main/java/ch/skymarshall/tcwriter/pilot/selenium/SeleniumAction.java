package ch.skymarshall.tcwriter.pilot.selenium;

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

import ch.skymarshall.tcwriter.pilot.AbstractGuiAction;

public class SeleniumAction extends AbstractGuiAction<WebElement> {

	private static final Duration QUICK_TIMEOUT = Duration.ofSeconds(5);
	private final GuiPilot pilot;
	private final By locator;

	protected static boolean canInteract(final WebElement element) {
		return element.isDisplayed() && element.isEnabled();
	}

	public SeleniumAction(final GuiPilot pilot, final By locator) {
		super(pilot);
		this.pilot = pilot;
		this.locator = locator;
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
	protected <U> PollingResult<U> waitActionSuccessLoop(final Predicate<WebElement> precondition,
			final Function<WebElement, PollingResult<U>> applier, final Duration timeout) {
		Duration remains = timeout;
		if (timeout.compareTo(QUICK_TIMEOUT) > 0) {
			final PollingResult<U> result = actionPoll(precondition, applier, QUICK_TIMEOUT); // quick polling
																								// first
			if (result.success()) {
				return result;
			}
			remains = remains.minus(QUICK_TIMEOUT);
		}
		return actionPoll(precondition, applier, remains);
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
	private <U> PollingResult<U> actionPoll(final Predicate<WebElement> precondition,
			final Function<WebElement, PollingResult<U>> applier, final Duration timeout) {
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

	protected <U> U executeInteractiveAction(final Function<WebElement, PollingResult<U>> applier) {
		return waitActionSuccess(SeleniumAction::canInteract, applier, pilot.getDefaultActionTimeout(), assertFail());
	}

	public SeleniumAction waitAvailable() {
		addReporting(e -> "Wait on " + locator);
		executeInteractiveAction(e -> value(Boolean.TRUE));
		return this;
	}

	public SeleniumAction click() {
		addReporting(e -> "Click on " + locator);
		executeInteractiveAction(action(WebElement::click));
		return this;
	}

	public boolean clickIfPresent(final Duration shortTimeout) {
		addReporting(e -> "Click on " + locator);
		final boolean found = waitActionSuccess(SeleniumAction::canInteract, action(WebElement::click), shortTimeout,
				r -> Boolean.FALSE);
		if (!found) {
			pilot.getActionReport().report("Not found: " + locator);
		}
		return found;
	}

}
