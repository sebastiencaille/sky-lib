package ch.scaille.tcwriter.pilot.selenium;

import static ch.scaille.tcwriter.pilot.Factories.Pollings.asserts;
import static ch.scaille.tcwriter.pilot.selenium.ElementPilot.click;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ch.scaille.tcwriter.pilot.ActionDelay;
import ch.scaille.tcwriter.pilot.Factories.Pollings;
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
			// noop
		}

		@Override
		public void assertFinished() {
			final var page = ExamplePage.this;
			page.polling(() -> page.enableTest, ElementPilot.isEnabled()).orFail();
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
	public void executeEnable() {
		polling(() -> this.enableTest, click().followedBy(new WaitEnableTestEnabledDelay())).orFail();
	}

	public void assertedEnabledTested() {
		polling(() -> this.enableTest, asserts(c -> Assertions.assertTrue(c.component.isEnabled()))).orFail();
	}

	public void testAlert() {
		// wait(() -> this.alertTest, WebElement::click);
		// wait(() -> this.alertTest, action(WebElement::click));
		// this one has nice reporting
		polling(() -> this.alertTest, click()).orFail();
	}

	public void clickOnMissingButton() {
		Assertions.assertFalse(
				polling(() -> this.notExistingElement,
						Pollings.<WebElement>exists().withTimeout(Duration.ofMillis(500))).isSatisfiedOr("not satisfied"),
				"isSatisfied should have returned false");
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
	public void assertDialogHandled() {
		pilot.waitModalDialogHandled();
	}

	public void assertElementChange() {
		polling(() -> this.elementChangeTest, click()).orFail();
		// Explicitly test using WebElement as source
		polling(() -> this.elementChange.findElement(By.id("TextChange")), textEquals("Hello again")).orFail();
	}

}