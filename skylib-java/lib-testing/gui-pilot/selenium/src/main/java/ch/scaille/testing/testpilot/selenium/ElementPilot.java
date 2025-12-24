package ch.scaille.testing.testpilot.selenium;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DomMutation;

import ch.scaille.testing.testpilot.AbstractComponentPilot;
import ch.scaille.testing.testpilot.Polling;
import ch.scaille.testing.testpilot.PolledComponent;
import ch.scaille.testing.testpilot.PollingResult;
import ch.scaille.testing.testpilot.factories.PollingResults;

@NullMarked
public class ElementPilot extends AbstractComponentPilot<WebElement> {

	private final SeleniumPilot pilot;

	@Nullable
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
	protected <R> Optional<PollingResult<WebElement, R>> loadComponent(Polling<WebElement, R>.InitializedPolling polling) {
		try {
			return super.loadComponent(polling);
		} catch (StaleElementReferenceException e) {
			invalidateCache();
			throw e;
		}
	}
	
	@Override
	protected Optional<String> getDescription() {
		final var description = getCachedElement().map(pilot::getElementPath);

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
	public boolean canCheck(final PolledComponent<WebElement> ctxt) {
		return ctxt.component().isDisplayed();
	}

	@Override
	protected <U> PollingResult<WebElement, U> waitPollingSuccessLoop(final Polling<WebElement, U> polling) {
		final var initializedPolling = polling.initializeFrom(this);
		return new SeleniumPoller(pilot.getDriver(), initializedPolling.getTimeout(), initializedPolling.getFirstDelay(),
				initializedPolling.getDelayFunction())
				.run(p -> executePolling(p, initializedPolling), PollingResult::isSuccess, PollingResults::failWithException)
				.orElseThrow();
	}

	@Override
	protected <U> PollingResult<WebElement, U> callPollingFunction(Polling<WebElement, U>.InitializedPolling polling) {
		try {
			return super.callPollingFunction(polling);
		} catch (final StaleElementReferenceException e) {
			invalidateCache();
			throw e;
		}
	}
	

	public static @Nullable String uniqueId(WebElement element) {
		 return element.getDomAttribute("data-__webdriver_id");
	}
	
	public void expectMutations(Predicate<DomMutation> filter) {
		pilot.expectMutations(mutation ->  
			loadGuiComponent().map(ElementPilot::uniqueId)
				.filter(c -> c.equals(uniqueId(mutation.getElement())))
				.isPresent() && filter.test(mutation));
	}

	public List<DomMutation> getMutations() {
		final var uid = loadGuiComponent().map(ElementPilot::uniqueId).orElse("");
		return pilot.getMutations(mutation -> uid.equals(uniqueId(mutation.getElement())));
	}

}
