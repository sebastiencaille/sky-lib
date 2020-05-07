package ch.skymarshall.tcwriter.pilot.selenium;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.skymarshall.tcwriter.pilot.AbstractGuiAction;

public class SeleniumAction extends AbstractGuiAction<WebElement> {

	private final GuiPilot pilot;
	private final By locator;

	protected static boolean isInteractive(final WebElement element) {
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

	@Override
	protected <U> Optional<U> waitActionProcessed(final Predicate<WebElement> precondition,
			final Function<WebElement, Optional<U>> applier, final Duration timeout) {
		return Optional.ofNullable(new WebDriverWait(pilot.getDriver(), timeout.getSeconds()).withTimeout(timeout)
				.pollingEvery(pollingTime(timeout))
				.ignoreAll(Arrays.asList(NoSuchElementException.class, StaleElementReferenceException.class))
				.until(d -> {
					try {
						final Optional<U> result = executeOnConditionUnsafe(precondition, applier);
						if (!result.isPresent()) {
							return null;
						}
						return result.get();
					} catch (final StaleElementReferenceException stale) {
						invalidateCache();
						throw stale;
					}
				}));
	}

	protected <U> U executeOnInteractive(final Function<WebElement, Optional<U>> applier) {
		return executeOnCondition(SeleniumAction::isInteractive, applier, pilot.getDefaultActionTimeout());
	}

	protected boolean executeOnInteractiveConsumer(final Consumer<WebElement> consumer) {
		return executeOnInteractive(e -> {
			consumer.accept(e);
			return Optional.of(Boolean.TRUE);
		});
	}

	public SeleniumAction click() {
		executeOnInteractiveConsumer(WebElement::click);
		return this;
	}

}
