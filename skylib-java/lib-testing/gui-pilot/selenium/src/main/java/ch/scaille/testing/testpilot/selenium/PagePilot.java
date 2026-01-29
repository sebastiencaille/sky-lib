package ch.scaille.testing.testpilot.selenium;

import java.util.Optional;
import java.util.function.Supplier;

import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.devtools.v142.network.model.DataReceived;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * Allows to pilot application using selenium's page concept
 */
@NullMarked
public class PagePilot {

	@Getter
    protected final SeleniumPilot pilot;

	private boolean invalid = true;

	public PagePilot(SeleniumPilot pilot) {
		this.pilot = pilot;
	}

	public WebDriver getDriver() {
		return pilot.getDriver();
	}

    public SeleniumPollingBuilder on(Supplier<WebElement> element) {
		return new SeleniumPollingBuilder(pilotOf(element));
	}

	public SeleniumPollingBuilder on(By by) {
		return new SeleniumPollingBuilder(pilotOf(driver -> driver.findElement(by)));
	}

	/**
	 * Creates a pilot to interact with a WebElement
	 */
	protected ElementPilot pilotOf(Supplier<WebElement> element) {
		return new ElementPilot(pilot) {
			@Override
			protected Optional<WebElement> loadGuiComponent() {
				reloadPage();
				final var webElement = element.get();
				// Not always respected
				if (webElement instanceof WrapsElement wrapsElement && wrapsElement.getWrappedElement() == null) {
					return Optional.empty();
				}
				return Optional.ofNullable(webElement);
			}

			@Override
			protected Optional<String> getDescription() {
				return super.getDescription().or(() -> Optional.of(element.get().toString()));
			}
			
			@Override
			protected void invalidateCache() {
				super.invalidateCache();
				invalid = true;
			}

			@Override
			public String toString() {
				return "Element of page " + getClass();
			}
		};
	}
	
	public SeleniumPollingBuilder on(ExpectedCondition<WebElement> conditions) {
		return new SeleniumPollingBuilder(pilotOf(conditions));
	}
	
	/**
	 * Creates a pilot to interact with a WebElement
	 */
	protected ElementPilot pilotOf(ExpectedCondition<WebElement> conditions) {
		return new ElementPilot(pilot) {
			@Override
			protected Optional<WebElement> loadGuiComponent() {

				return Optional.ofNullable(conditions.apply(pilot.getDriver()));
			}

			@Override
			protected Optional<String> getDescription() {
				return super.getDescription().or(() -> Optional.of(conditions.toString()));
			}
			
			@Override
			protected void invalidateCache() {
				super.invalidateCache();
				invalid = true;
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

}
