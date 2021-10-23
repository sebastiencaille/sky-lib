package ch.skymarshall.tcwriter.pilot.selenium;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.support.PageFactory;

import ch.skymarshall.tcwriter.pilot.EditionPolling;
import ch.skymarshall.tcwriter.pilot.Polling;

public class PagePilot<T> {

	private Class<T> pageClass;
	private SeleniumGuiPilot pilot;
	private T page;

	public PagePilot(SeleniumGuiPilot pilot, Class<T> pageClass) {
		this.pilot = pilot;
		this.pageClass = pageClass;
	}

	public T page() {
		return loadPage();
	}

	public ElementPilot element(Function<T, WebElement> toElement) {
		return new ElementPilot(pilot) {
			@Override
			protected WebElement loadGuiComponent() {
				WebElement webElement = toElement.apply(loadPage());
				if (((WrapsElement) webElement).getWrappedElement() == null) {
					return null;
				}
				return webElement;
			}

			@Override
			protected void invalidateCache() {
				super.invalidateCache();
				page = null;
			}

			@Override
			protected String getDescription() {
				String result = super.getDescription();
				if (result == null) {
					result =  toElement.apply(loadPage()).toString();
				}
				return result;
			}
			
			@Override
			public String toString() {
				return "Element of page " + pageClass;
			}
		};
	}

	private T loadPage() {
		if (page == null) {
			page = PageFactory.initElements(pilot.getDriver(), pageClass);
		}
		return page;
	}

	public <U> U wait(Function<T, WebElement> toElement, final Polling<WebElement, U> polling) {
		return element(toElement).wait(polling);
	}

	public boolean wait(Function<T, WebElement> toElement, Consumer<WebElement> action) {
		return element(toElement).wait(EditionPolling.action(action).withName("<anonymous action>"));
	}

	public boolean ifEnabled(Function<T, WebElement> toElement, final Polling<WebElement, Boolean> polling,
			final Duration shortTimeout) {
		return element(toElement).ifEnabled(polling, shortTimeout);
	}

}
