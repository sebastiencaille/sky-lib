package ch.skymarshall.tcwriter.pilot.selenium;

import java.time.Duration;
import java.util.Arrays;
import java.util.function.Consumer;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.skymarshall.tcwriter.pilot.AbstractGuiComponent;
import ch.skymarshall.tcwriter.pilot.Factories;
import ch.skymarshall.tcwriter.pilot.Polling;
import ch.skymarshall.tcwriter.pilot.PollingResult;

public class ElementPilot extends AbstractGuiComponent<ElementPilot, WebElement> {

	private static final Duration SHORT_POLLING_TIMEOUT = Duration.ofSeconds(5);
	private final SeleniumGuiPilot pilot;
	private final By locator;

	public ElementPilot(final SeleniumGuiPilot pilot, final By locator) {
		super(pilot);
		this.pilot = pilot;
		this.locator = locator;
	}
	
	public ElementPilot(final SeleniumGuiPilot pilot) {
		super(pilot);
		this.pilot = pilot;
		this.locator = null;
	}

	@Override
	protected String getDescription() {
		String description = super.getDescription();
		if (description == null && locator != null) {
			return locator.toString();
		}
		return description;
	}

	@Override
	public String toString() {
		return getDescription();
	}

	@Override
	protected String reportNameOf(WebElement c) {
		return c.toString();
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
			return Factories.value(new WebDriverWait(pilot.getDriver(), timeout) //
					.pollingEvery(pollingTime(timeout)) //
					.ignoreAll(Arrays.asList(NoSuchElementException.class, StaleElementReferenceException.class,
							ElementNotInteractableException.class, UnhandledAlertException.class))
					.until(d -> {
						try {
							return executePolling(polling).orElse(null);
						} catch (final StaleElementReferenceException e) {
							invalidateCache();
							throw e;
						}
					}));
		} catch (final TimeoutException e) {
			return Factories.onException(e.getCause());
		}
	}

	public boolean wait(Consumer<WebElement> action) {
		return wait(Factories.action(action));
	}

	public static Polling<WebElement, Boolean> isEnabled() {
		return Factories.<WebElement>satisfies(WebElement::isEnabled).withReportText("is enabled");
	}

	public static Polling<WebElement, Boolean> click() {
		return Factories.action(WebElement::click).withReportText("clicked");
	}

	public boolean run(Consumer<WebElement> action, String name) {
		return wait(Factories.action(action).withReportText(name));
	}

}
