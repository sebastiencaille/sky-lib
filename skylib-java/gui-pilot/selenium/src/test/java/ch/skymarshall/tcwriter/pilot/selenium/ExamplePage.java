package ch.skymarshall.tcwriter.pilot.selenium;

import static ch.skymarshall.tcwriter.pilot.EditionPolling.action;
import static ch.skymarshall.tcwriter.pilot.selenium.ElementPilot.click;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ch.skymarshall.tcwriter.pilot.ActionDelay;
import ch.skymarshall.tcwriter.pilot.ModalDialogDetector;

public class ExamplePage extends PagePilot<ExamplePage> {

	@FindBy(id = "Proceed")
	public WebElement proceed;

	@FindBy(id = "OK")
	public WebElement ok;

	@FindBy(id = "NotExisting")
	public WebElement notExisting;

	public ExamplePage(SeleniumGuiPilot pilot) {
		super(pilot);
	}

	public class ProceedEnabledDelay implements ActionDelay {

		public ProceedEnabledDelay() {
		}

		@Override
		public boolean waitFinished() {
			ExamplePage page = ExamplePage.this;
			page.wait(() -> page.proceed, WebElement::isEnabled);
			Assertions.assertTrue(page.proceed.isEnabled(), () -> "Proceed is enabled");
			return true;
		}

		@Override
		public String toString() {
			return "Wait on Proceed enabled";
		}

	}

	/**
	 * Perform a click, and tell the next action that the next action must wait
	 * until "Proceed" is enabled
	 */
	public void proceed() {
		wait(() -> this.proceed, click().followedBy(new ProceedEnabledDelay()));
	}

	public void ok(ExamplePage mainPage) {
		// mainPage.element(p -> p.ok).wait(WebElement::click);
		// mainPage.element(p -> p.ok).wait(click());
		// mainPage.element(p -> p.ok).wait(action(WebElement::click));
		mainPage.wait(() -> this.ok, WebElement::click);
	}

	public void clickOnMissingButton(ExamplePage mainPage) {
		mainPage.ifEnabled(() -> this.notExisting, action(WebElement::click), Duration.ofMillis(500));
	}

	public void expectedOkDialog() {
		pilot.expectModalDialog(s -> {
			s.doAcknowledge();
			return ModalDialogDetector.expected();
		});
	}

	/**
	 * Handle the modal dialog raised by the click on OK
	 */
	public void checkDialogHandled() {
		pilot.waitModalDialogHandled();
	}

}