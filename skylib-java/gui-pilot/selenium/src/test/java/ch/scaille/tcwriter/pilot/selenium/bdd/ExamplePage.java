package ch.scaille.tcwriter.pilot.selenium.bdd;

import static ch.scaille.tcwriter.pilot.Factories.action;
import static ch.scaille.tcwriter.pilot.selenium.ElementPilot.click;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ch.scaille.tcwriter.pilot.ActionDelay;
import ch.scaille.tcwriter.pilot.Factories;
import ch.scaille.tcwriter.pilot.ModalDialogDetector;
import ch.scaille.tcwriter.pilot.selenium.ElementPilot;
import ch.scaille.tcwriter.pilot.selenium.PagePilot;
import ch.scaille.tcwriter.pilot.selenium.SeleniumPilot;
import ch.scaille.tcwriter.pilot.selenium.bdd.Scenario.ScenarioFactory;

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

	public class EnableTestDelay implements ActionDelay {

		public EnableTestDelay() {
		}

		@Override
		public boolean waitFinished() {
			ExamplePage page = ExamplePage.this;
			page.wait(() -> page.enableTest, ElementPilot.isEnabled());
			Assertions.assertTrue(page.enableTest.isEnabled(), () -> "EnableTest is enabled");
			return true;
		}

		@Override
		public String toString() {
			return "Wait until EnableTest enabled";
		}

	}

	private static final ScenarioFactory<ExamplePage> SCENARIO_FACTORY = Scenario.of(ExamplePage::new);

	public static final Scenario<ExamplePage> SCENARIO_CONNECT = SCENARIO_FACTORY
			.with(p -> p.getDriver().get("http://localhost:8080/example1.html"), ExamplePage::checkEnable);

	public static final Scenario<ExamplePage> SCENARIO_ENABLE = SCENARIO_FACTORY.with(ExamplePage::testEnable,
			ExamplePage::checkEnable);

	public static final Scenario<ExamplePage> SCENARIO_ALERT = SCENARIO_FACTORY.with(ExamplePage::expectTestAlertDialog,
			ExamplePage::testAlert, ExamplePage::checkDialogHandled);

	/**
	 * Perform a click, and tell the next action that the next action must wait
	 * until "Proceed" is enabled
	 */
	public void testEnable() {
		wait(() -> this.enableTest, click().followedBy(new EnableTestDelay()));
	}

	/**
	 * Perform a click, and tell the next action that the next action must wait
	 * until "Proceed" is enabled
	 */
	public void checkEnable() {
		wait(() -> this.enableTest, Factories.satisfies(e -> e.isEnabled()));
	}

	public void testAlert() {
		// wait(() -> this.alertTest, WebElement::click);
		// wait(() -> this.alertTest, action(WebElement::click));
		// this one has nice reporting
		wait(() -> this.alertTest, click());
	}

	public void clickOnMissingButton() {
		ifEnabled(() -> this.notExistingElement, action(WebElement::click).withTimeout(Duration.ofMillis(500)));
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

	public void elementChangeTest() {
		wait(() -> this.elementChangeTest, click());
		// Explicitly test using WebElement as source
		wait(() -> this.elementChange.findElement(By.id("TextChange")), textEquals("Hello again"));
	}

}