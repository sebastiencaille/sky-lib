package ch.skymarshall.tcwriter.pilot.selenium;

import java.util.function.Consumer;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import ch.skymarshall.tcwriter.pilot.AbstractComponentPilot;
import ch.skymarshall.tcwriter.pilot.Factories;
import ch.skymarshall.tcwriter.pilot.Polling;
import ch.skymarshall.tcwriter.pilot.PollingResult;

public class ElementPilot extends AbstractComponentPilot<ElementPilot, WebElement> {

	private final SeleniumPilot pilot;
	private final By locator;

	public ElementPilot(final SeleniumPilot pilot, final By locator) {
		super(pilot);
		this.pilot = pilot;
		this.locator = locator;
	}

	public ElementPilot(final SeleniumPilot pilot) {
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

	@Override
	protected <U> PollingResult<WebElement, U> waitPollingSuccessLoop(final Polling<WebElement, U> polling) {
		polling.initialize(this);
		return new SeleniumPoller(pilot.getDriver(), polling.getTimeout(), polling.getFirstDelay(),
				polling.getDelayFunction()).run(p -> executePolling(p, polling), PollingResult::isSuccess,
						Factories::onException);
	}

	@Override
	protected <U> PollingResult<WebElement, U> callPollingFunction(Polling<WebElement, U> polling) {
		try {
			return super.callPollingFunction(polling);
		} catch (final StaleElementReferenceException e) {
			invalidateCache();
			throw e;
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
