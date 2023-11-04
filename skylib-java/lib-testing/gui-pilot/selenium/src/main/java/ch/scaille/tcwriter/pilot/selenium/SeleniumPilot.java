package ch.scaille.tcwriter.pilot.selenium;

import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import ch.scaille.tcwriter.pilot.ModalDialogDetector;
import ch.scaille.tcwriter.pilot.ModalDialogDetector.PollingResult;

public class SeleniumPilot extends ch.scaille.tcwriter.pilot.GuiPilot {

	private final WebDriver driver;

	public SeleniumPilot(final WebDriver driver) {
		this.driver = driver;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public ElementPilot element(final By locator) {
		return new ElementPilot(this, locator);
	}

	public AlertPilot alert() {
		return new AlertPilot(this);
	}

	/**
	 * Creates a page (method/constructor that takes a SeleniumPilot as parameter) 
	 * @param <C>
	 * @param factory
	 * @return
	 */
	public <C extends PagePilot> C page(Function<SeleniumPilot, C> factory) {
		return factory.apply(this);
	}

	@Override
	protected ModalDialogDetector createDefaultModalDialogDetector() {
		final var testThread = Thread.currentThread();
		return new ModalDialogDetector(() -> AlertDetector.listAlerts(this, null), e -> testThread.interrupt());
	}

	public ModalDialogDetector expectModalDialog(final Function<AlertPilot, PollingResult> check) {
		final var testThread = Thread.currentThread();
		return expectModalDialog(new ModalDialogDetector(() -> AlertDetector.listAlerts(this, check),
				e -> testThread.interrupt()));
	}

}
