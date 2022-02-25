package ch.scaille.tcwriter.pilot.selenium;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.support.PageFactory;

import ch.scaille.tcwriter.pilot.Factories.Pollings;
import ch.scaille.tcwriter.pilot.Polling;
import ch.scaille.tcwriter.pilot.PollingResult.FailureHandler;

@SuppressWarnings("java:S5960")
public class PagePilot {

	protected final SeleniumPilot pilot;

	private boolean invalid = true;

	public PagePilot(SeleniumPilot pilot) {
		this.pilot = pilot;
	}

	public WebDriver getDriver() {
		return pilot.getDriver();
	}

	public SeleniumPilot getPilot() {
		return pilot;
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

	public <U> U wait(Supplier<WebElement> element, final Polling<WebElement, U> polling,
			FailureHandler<WebElement, U> failureHandler) {
		return element(element).wait(polling, failureHandler);
	}

	public boolean wait(Supplier<WebElement> element, Consumer<WebElement> action) {
		return element(element).wait(Pollings.action(action).withReportText("unnamed action"));
	}

	public boolean ifEnabled(Supplier<WebElement> element, final Polling<WebElement, Boolean> polling) {
		return element(element).ifEnabled(polling);
	}

	public Polling<WebElement, Boolean> textEquals(String expected) {
		return Pollings
				.<WebElement>assertion(pc -> Assertions.assertEquals(expected, pc.component.getText(), pc.description))
				.withReportText("text " + expected);
	}
}
