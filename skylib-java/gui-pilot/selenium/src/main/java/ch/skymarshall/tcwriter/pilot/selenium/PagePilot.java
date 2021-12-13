package ch.skymarshall.tcwriter.pilot.selenium;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.support.PageFactory;

import ch.skymarshall.tcwriter.pilot.ActionPolling;
import ch.skymarshall.tcwriter.pilot.Polling;
import ch.skymarshall.tcwriter.pilot.StatePolling;

public class PagePilot {

	protected final SeleniumGuiPilot pilot;

	private boolean invalid = true;

	public PagePilot(SeleniumGuiPilot pilot) {
		this.pilot = pilot;
	}

	public ElementPilot element(Supplier<WebElement> element) {
		return new ElementPilot(pilot) {
			@Override
			protected WebElement loadGuiComponent() {
				reloadPage();
				WebElement webElement = element.get();
				if (webElement instanceof WrapsElement && ((WrapsElement) webElement).getWrappedElement() == null) {
					return null;
				}
				return webElement;
			}

			@Override
			protected void invalidateCache() {
				super.invalidateCache();
				invalid = true;
			}

			@Override
			protected String getDescription() {
				String result = super.getDescription();
				if (result == null) {
					result = element.get().toString();
				}
				return result;
			}

			@Override
			public String toString() {
				return "Element of page " + getClass();
			}
		};
	}

	private void reloadPage() {
		if (invalid) {
			PageFactory.initElements(pilot.getDriver(), this);
			invalid = false;
		}
	}

	public <U> U wait(Supplier<WebElement> element, final Polling<WebElement, U> polling) {
		return element(element).wait(polling);
	}

	public boolean wait(Supplier<WebElement> element, Consumer<WebElement> action) {
		return element(element).wait(ActionPolling.action(action).withReportText("unnamed action"));
	}

	public boolean ifEnabled(Supplier<WebElement> element, final Polling<WebElement, Boolean> polling,
			final Duration shortTimeout) {
		return element(element).ifEnabled(polling, shortTimeout);
	}
	
	public Polling<WebElement, Boolean> textEquals(String expected) {
		return StatePolling.<WebElement>assertion(e -> Assertions.assertEquals(expected, e.getText()))
				.withReportText("text " + expected);
	}
}
