package ch.scaille.testing.testpilot.selenium;

import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import ch.scaille.testing.testpilot.ModalDialogDetector;
import ch.scaille.testing.testpilot.ModalDialogDetector.PollingResult;
import ch.scaille.util.helpers.NoExceptionCloseable;

public class SeleniumPilot extends ch.scaille.testing.testpilot.GuiPilot {

	private final WebDriver driver;

	public SeleniumPilot(final WebDriver driver) {
		this.driver = driver;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public ElementPilot element(final By locator) {
		return new ElementPilot(this, webDriver -> webDriver.findElement(locator));
	}
	
	public ElementPilot element(final ExpectedCondition<WebElement> expectedCondition) {
		return new ElementPilot(this, expectedCondition);
	}

	public AlertPilot alert() {
		return new AlertPilot(this);
	}

	/**
	 * Creates a page (method/constructor that takes a SeleniumPilot as parameter)
	 */
	public <C extends PagePilot> C page(Function<SeleniumPilot, C> factory) {
		return factory.apply(this);
	}

	@Override
	protected ModalDialogDetector.Builder createDefaultModalDialogDetector() {
		final var testThread = Thread.currentThread();
		return new ModalDialogDetector.Builder(() -> AlertDetector.listAlerts(this, null), e -> testThread.interrupt());
	}

	/**
	 * This api can be used with try/finally
	 */
	public NoExceptionCloseable expectModalDialog(final Function<AlertPilot, PollingResult> check) {
		final var testThread = Thread.currentThread();
		return expectModalDialog(new ModalDialogDetector.Builder(() -> AlertDetector.listAlerts(this, check),
				e -> testThread.interrupt()));
	}


}
