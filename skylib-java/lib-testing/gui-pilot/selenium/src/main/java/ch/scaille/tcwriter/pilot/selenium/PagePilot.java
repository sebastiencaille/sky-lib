package ch.scaille.tcwriter.pilot.selenium;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.support.PageFactory;

import ch.scaille.tcwriter.pilot.AbstractComponentPilot;
import ch.scaille.tcwriter.pilot.Factories.Pollings;
import ch.scaille.tcwriter.pilot.Polling;

/**
 * Allows to pilot application using selenium's page concept
 */
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

	/**
	 * Creates a pilot to interact with a WebElement
	 */
	public ElementPilot element(Supplier<WebElement> element) {
		return new ElementPilot(pilot) {
			@Override
			protected Optional<WebElement> loadGuiComponent() {
				reloadPage();
				final var webElement = element.get();
				if (webElement instanceof WrapsElement && ((WrapsElement) webElement).getWrappedElement() == null) {
					return Optional.empty();
				}
				return Optional.ofNullable(webElement);
			}

			@Override
			protected void invalidateCache() {
				super.invalidateCache();
				invalid = true;
			}

			@Override
			protected String getDescription() {
				var description = super.getDescription();
				if (description == null) {
					description = element.get().toString();
				}
				return description;
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

	public <U> AbstractComponentPilot<ElementPilot, WebElement>.Wait<U> polling(Supplier<WebElement> element,
			final Polling<WebElement, U> polling) {
		return element(element).polling(polling);
	}

	public AbstractComponentPilot<ElementPilot, WebElement>.Wait<Boolean> polling(Supplier<WebElement> element, Consumer<WebElement> action) {
		return element(element).polling(Pollings.applies(action));
	}

	public static Polling<WebElement, Boolean> textEquals(String expected) {
		return Pollings
				.<WebElement>asserts(pc -> Assertions.assertEquals(expected, pc.component.getText(), pc.description))
				.withReportText("text " + expected);
	}
}
