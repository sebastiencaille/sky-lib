package ch.scaille.tcwriter.pilot.selenium;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import ch.scaille.tcwriter.pilot.AbstractEvent.EventWaiter;
import ch.scaille.tcwriter.pilot.ActionDelay;
import ch.scaille.tcwriter.pilot.ModalDialogDetector;
import ch.scaille.tcwriter.pilot.factories.Pollings;
import ch.scaille.tcwriter.pilot.selenium.BiDiEvent.BiDiEventConfig;
import ch.scaille.tcwriter.pilot.selenium.BiDiEvent.IBiDiEvent;
import ch.scaille.tcwriter.pilot.selenium.BiDiEvent.MutationConfig;

public class ExamplePage extends PagePilot {

	private static final By ENABLE_TEST = By.id("EnableTest");

	private static final By ALERT_TEST = By.id("AlertTest");

	private static final By ELEMENT_CHANGE_TEST = By.id("ElementChangeTest");

	private static final By ELEMENT_CHANGE = By.id("ElementChange");

	private static final By NOT_EXISTING = By.id("NotExisting");

	private static final String TEXT_XPATH = "//div[@id='ElementChangeHolder']";

	private enum TestEvents implements IBiDiEvent {

		DISPLAY_HELLO(new BiDiEventConfig(TEXT_XPATH, MutationConfig.CHILD_LIST, false,
				"mutation => {console.log(mutation); return mutation.target.children[0].textContent === 'Hello' }")),
		DISPLAY_HELLO_AGAIN(new BiDiEventConfig(TEXT_XPATH, MutationConfig.CHILD_LIST, false,
				"mutation => mutation.target.children[0].textContent === 'Hello again'"));

		private final BiDiEventConfig biDiEventConfig;

		TestEvents(BiDiEventConfig biDiEventConfig) {
			this.biDiEventConfig = biDiEventConfig;
		}

		@Override
		public BiDiEventConfig config() {
			return biDiEventConfig;
		}

	}

	private BiDiEvent<TestEvents> helloEventListener;

	public ExamplePage(SeleniumPilot pilot) {
		super(pilot);
		helloEventListener = new BiDiEvent<>(pilot, TestEvents.DISPLAY_HELLO, TestEvents.DISPLAY_HELLO_AGAIN);
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
	 * Perform a click, and tell the next action that the next action must wait
	 * until "Proceed" is enabled
	 */
	public void executeEnable() {
		on(elementToBeClickable(ENABLE_TEST)).configure(p -> p.andThen(new WaitEnableTestEnabledDelay())).click();
	}

	public void assertedEnabledTested() {
		on(visibilityOfElementLocated(ENABLE_TEST)).assertPresent();
	}

	public void testAlert() {
		on(elementToBeClickable(ALERT_TEST)).click();
	}

	public void clickOnMissingButton() {
		Assertions.assertFalse(
				on(visibilityOfElementLocated(NOT_EXISTING)).report("isSatisfied should have returned false")
						.ifNot()
						.satisfied(Pollings.<WebElement>exists().withTimeout(Duration.ofMillis(500))));
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

	public void assertElementChange() throws InterruptedException {
		final EventWaiter<TestEvents> eventsWaiter = helloEventListener.expect(h -> h.size() == 2);
		on(elementToBeClickable(ELEMENT_CHANGE_TEST)).fail().ifNot().clicked();
		// Explicitly test using WebElement as source
		eventsWaiter.assertMatches();
		on(visibilityOfElementLocated(ELEMENT_CHANGE)).fail().ifNot().textEquals("Hello again");
	}

}