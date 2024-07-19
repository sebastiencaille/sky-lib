package ch.scaille.tcwriter.pilot.selenium;

import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ch.scaille.tcwriter.pilot.PollingBuilder;
import ch.scaille.tcwriter.pilot.PollingContext;
import ch.scaille.tcwriter.pilot.factories.Pollings;

public class SeleniumPollingBuilder extends
		PollingBuilder<WebElement, SeleniumPollingBuilder,SeleniumPollingBuilder.WebElementPoller> {

	public static Predicate<PollingContext<WebElement>> satisfies(
			Function<WebElement, ExpectedCondition<WebElement>> expectedCondition) {
		return context -> expectedCondition.apply(context.getComponent())
				.apply(context.getGuiPilot(SeleniumPilot.class).getDriver()) != null;
	}

	public static class WebElementPoller extends PollingBuilder.Poller<WebElement> {

		protected WebElementPoller(PollingBuilder<WebElement, ?, ?> builder) {
			super(builder);
		}

		public boolean present() {
			return satisfied(Pollings.exists());
		}

		public boolean isEnabled() {
			return configure(polling -> polling.withReportText("is enabled"))
					.satisfiedCtxt(satisfies(ExpectedConditions::elementToBeClickable));
		}

		public boolean clicked() {
			return configure(polling -> polling.withReportText("clicked")).applied(WebElement::click);
		}

		public boolean textEquals(String text) {
			return asserted(context -> Assertions.assertEquals(text, context.getComponent().getText(),
					"text equals '" + text + "'"));
		}

	}

	public SeleniumPollingBuilder(ElementPilot elementPilot) {
		super(elementPilot);
	}

	@Override
	public WebElementPoller ifNot() {
		return new WebElementPoller(this);
	}

	public void click() {
		fail().ifNot().clicked();
	}

	public void assertPresent() {
		fail().ifNot().present();
	}

}
