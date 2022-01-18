package ch.scaille.testing.bdd.selenium;

import static ch.scaille.tcwriter.pilot.Factories.action;
import static ch.scaille.tcwriter.pilot.selenium.ElementPilot.click;
import static ch.scaille.testing.bdd.definition.Scenario.step;

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
import ch.scaille.testing.bdd.definition.Scenario;
import ch.scaille.testing.bdd.definition.Scenario.ScenarioFactory;

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

	/** BDD **/

	private static final ScenarioFactory<SeleniumPilot, ExamplePage> BDD_FACTORY = Scenario.of(ExamplePage::new);

	public static final Scenario<SeleniumPilot, ExamplePage> OPEN_WEBSITE = BDD_FACTORY.with(
			step("I open the website", p -> p.getDriver().get("http://localhost:8080/example1.html")),
			step("I see that the website is open", ExamplePage::testEnabled));

	public static final Scenario<SeleniumPilot, ExamplePage> TEST_ENABLE = BDD_FACTORY.with(
			step("I test the Enable function", ExamplePage::testEnable),
			step("I see that the Enable function is back to normal after some seconds", ExamplePage::testEnabled));
	
	public static final Scenario<SeleniumPilot, ExamplePage> TEST_ALERT = BDD_FACTORY.with(
			step("I expect the Alert", ExamplePage::expectTestAlertDialog),
			step("I test the Alert function", ExamplePage::testAlert),
			step("I see that the Alert was raised|I acknowledge the Alert", ExamplePage::checkDialogHandled));


	public class EnableTestDelay extends ActionDelay {

		public EnableTestDelay() {
		}

		@Override
		public void waitFinished() {
			ExamplePage page = ExamplePage.this;
			page.wait(() -> page.enableTest, ElementPilot.isEnabled());
			Assertions.assertTrue(page.enableTest.isEnabled(), () -> "EnableTest is enabled");
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
		wait(() -> this.enableTest, click().followedBy(new EnableTestDelay()));
	}

	public void testEnabled() {
		wait(() -> this.enableTest, Factories.assertion(c -> Assertions.assertTrue(c.component.isEnabled())));
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