package ch.scaille.tcwriter.pilot.selenium;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import ch.scaille.testing.testpilot.ActionDelay;
import ch.scaille.testing.testpilot.ModalDialogDetector;
import ch.scaille.testing.testpilot.factories.Pollings;
import ch.scaille.testing.testpilot.selenium.PagePilot;
import ch.scaille.testing.testpilot.selenium.SeleniumPilot;
import ch.scaille.testing.testpilot.selenium.SeleniumPollingBuilder;

public class ExamplePage extends PagePilot {

	private static final By ENABLE_TEST = By.id("EnableTest");

	private static final By ALERT_TEST = By.id("AlertTest");

	private static final By ELEMENT_CHANGE_TEST = By.id("ElementChangeTest");

	private static final By NOT_EXISTING = By.id("NotExisting");

	private static final By TEXT_XPATH = By.xpath("//div[@id='TextChange']");

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
			page.on(elementToBeClickable(ENABLE_TEST)).assertPresent();
			Assertions.assertTrue(visibilityOfElementLocated(ENABLE_TEST).apply(getDriver()).isEnabled(),
					"EnableTest is enabled");
		}

		@Override
		public String toString() {
			return "Wait until EnableTest enabled";
		}

	}

	/**
	 * Perform a click and tell the next action that the next action must wait
	 * until "Proceed" is enabled
	 */
	public void executeEnable() {
		on(elementToBeClickable(ENABLE_TEST))
			.withConfig(p -> p.andThen(new WaitEnableTestEnabledDelay()))
			.failUnless().clicked();
	}

	public void assertedEnabledTested() {
		on(visibilityOfElementLocated(ENABLE_TEST)).assertPresent();
	}

	public void testAlert() {
		on(elementToBeClickable(ALERT_TEST)).click();
	}

	public void clickOnMissingButton() {
		Assertions.assertFalse(
				on(visibilityOfElementLocated(NOT_EXISTING))
					.withConfig(p -> p.timeout(Duration.ofSeconds(2)))
					.evaluateWithReport("Button does not exist").that()
					.satisfied(Pollings.<WebElement>exists().timeout(Duration.ofMillis(500))));
	}

	/**
	 * use with assertDialogHandled();
	 */
	public void expectTestAlertDialog() {
		// closed by expectTestAlertDialog
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
		var changedElement = on(driver -> driver.findElement(TEXT_XPATH));
		changedElement.expectMutations(mutation -> "textContent".equals(mutation.getAttributeName()));
		on(elementToBeClickable(ELEMENT_CHANGE_TEST)).failUnless().clicked();
		// Explicitly test using WebElement as source
		changedElement.failUnless().assertedCtxt(SeleniumPollingBuilder.assertMutations(mutations -> 
			Assertions.assertEquals(2, mutations.size(), mutations::toString)));
		changedElement.failUnless().textEquals("Hello again");
	}

}