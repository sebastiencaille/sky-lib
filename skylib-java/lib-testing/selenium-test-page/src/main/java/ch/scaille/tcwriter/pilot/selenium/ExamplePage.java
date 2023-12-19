package ch.scaille.tcwriter.pilot.selenium;

import static ch.scaille.tcwriter.pilot.Factories.Pollings.action;
import static ch.scaille.tcwriter.pilot.Factories.Pollings.assertion;
import static ch.scaille.tcwriter.pilot.selenium.ElementPilot.click;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ch.scaille.tcwriter.pilot.ActionDelay;
import ch.scaille.tcwriter.pilot.ModalDialogDetector;

public class ExamplePage extends PagePilot {

	@FindBy(id = "EnableTest")
	public WebElement enableTest;

	@FindBy(id = "AlertTest")
	public WebElement alertTest;

	@FindBy(id = "ElementChangeTest")
	public WebElement elementChangeTest;

	@FindBy(id = "ElementChange")
	public WebElement elementChange;

	@FindBy(id = "NotExisting")
	public WebElement notExistingElement;

	public ExamplePage(SeleniumPilot pilot) {
		super(pilot);
	}

	public class WaitEnableTestEnabledDelay extends ActionDelay {

		public WaitEnableTestEnabledDelay() {
			//noop
		}

		@Override
		public void waitFinished() {
			final var page = ExamplePage.this;
			page.waitOn(() -> page.enableTest, ElementPilot.isEnabled());
			Assertions.assertTrue(page.enableTest.isEnabled(), "EnableTest is enabled");
		}

		@Override
		public String toString() {
			return "Wait until EnableTest enabled";
		}

	}

	/**
	 * Perform a click, and tell the next action that the next action must wait
	 * until "Proceed" is enabled
	 */
	public void testEnable() {
		waitOn(() -> this.enableTest, click().followedBy(new WaitEnableTestEnabledDelay()));
	}

	public void testEnabled() {
		waitOn(() -> this.enableTest, assertion(c -> Assertions.assertTrue(c.component.isEnabled())));
	}

	public void testAlert() {
		// wait(() -> this.alertTest, WebElement::click);
		// wait(() -> this.alertTest, action(WebElement::click));
		// this one has nice reporting
		waitOn(() -> this.alertTest, click());
	}

	public void clickOnMissingButton() {
		Assertions.assertFalse(ifEnabled(() -> this.notExistingElement, action(WebElement::click).withTimeout(Duration.ofMillis(500))), "Result must be false");
	}

	public void expectTestAlertDialog() {
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

	public void testElementChange() {
		waitOn(() -> this.elementChangeTest, click());
		// Explicitly test using WebElement as source
		waitOn(() -> this.elementChange.findElement(By.id("TextChange")), textEquals("Hello again"));
	}

}