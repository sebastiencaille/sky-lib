package ch.scaille.tcwriter.pilot.selenium;

import java.util.Optional;
import java.util.function.Function;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ch.scaille.tcwriter.pilot.AbstractComponentPilot;
import ch.scaille.tcwriter.pilot.Polling;
import ch.scaille.tcwriter.pilot.PollingBuilder;
import ch.scaille.tcwriter.pilot.PollingContext;
import ch.scaille.tcwriter.pilot.PollingResult;
import ch.scaille.tcwriter.pilot.factories.PollingResults;

public class ElementPilot extends AbstractComponentPilot<ElementPilot, WebElement> {

	private final SeleniumPilot pilot;
	private final Function<WebDriver, WebElement> locator;

	public ElementPilot(final SeleniumPilot pilot, Function<WebDriver, WebElement> locator) {
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
	protected Optional<String> getDescription() {
		final var description = getCachedElement().map(Object::toString);
		if (locator != null) {
			return description.or(() -> Optional.of(locator.toString()));
		}
		return description;
	}

	@Override
	public String toString() {
		return getDescription().orElse("<unidentified>");
	}

	@Override
	protected Optional<WebElement> loadGuiComponent() {
        return Optional.ofNullable(locator).map(l -> l.apply(pilot.getDriver()));
	}

	@Override
	protected boolean canCheck(final PollingContext<WebElement> ctxt) {
		return ctxt.getComponent().isDisplayed();
	}

	@Override
	public PollingBuilder<ElementPilot, WebElement, SeleniumPollingBuilder, SeleniumPollingBuilder.WebElementPoller> polling() {
		return new SeleniumPollingBuilder(this);
	}
	
	@Override
	protected <U> PollingResult<WebElement, U> waitPollingSuccessLoop(final Polling<WebElement, U> polling) {
		polling.initialize(this);
		return new SeleniumPoller(pilot.getDriver(), polling.getTimeout(), polling.getFirstDelay(),
				polling.getDelayFunction())
				.run(p -> executePolling(p, polling), PollingResult::isSuccess, PollingResults::failWithException)
				.orElseThrow();
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

}
