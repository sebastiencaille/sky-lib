package ch.scaille.testing.testpilot.selenium;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DomMutation;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ch.scaille.testing.testpilot.PollingBuilder;
import ch.scaille.testing.testpilot.PolledComponent;
import ch.scaille.testing.testpilot.factories.Pollings;

public class SeleniumPollingBuilder extends
		PollingBuilder<WebElement, SeleniumPollingBuilder, SeleniumPollingBuilder.WebElementPoller, PollingBuilder.DefaultConfigurer<WebElement>> {

	public static Predicate<PolledComponent<WebElement>> satisfies(
			Function<WebElement, ExpectedCondition<WebElement>> expectedCondition) {
		return context -> expectedCondition.apply(context.component())
				.apply(context.getGuiPilot(SeleniumPilot.class).getDriver()) != null;
	}

	public static class WebElementPoller extends PollingBuilder.Poller<WebElement> {

		protected WebElementPoller(PollingBuilder<WebElement, ?, ?, ?> builder) {
			super(builder);
		}

		public boolean present() {
			return satisfied(Pollings.exists());
		}

		public boolean isEnabled() {
			return configure(polling -> polling.reportText("is enabled"))
					.satisfiedCtxt(satisfies(ExpectedConditions::elementToBeClickable));
		}

		public boolean clicked() {
			return configure(polling -> polling.reportText("clicked")).applied(WebElement::click);
		}

		public boolean textEquals(String text) {
			return asserted(
					component -> Assertions.assertEquals(text, component.getText(), "text equals '" + text + "'"));
		}

	}
	
	public static Consumer<PolledComponent<WebElement>> mutations(Predicate<List<DomMutation>> mutationsTest) {
		return ctxt -> mutationsTest.test(((ElementPilot) ctxt.componentPilot()).getMutations());
	}
	
	public static Consumer<PolledComponent<WebElement>> assertMutations(Consumer<List<DomMutation>> mutationsTest) {
		return ctxt -> mutationsTest.accept(((ElementPilot) ctxt.componentPilot()).getMutations());
	}

	private final ElementPilot elementPilot;

	public SeleniumPollingBuilder(ElementPilot elementPilot) {
		super(elementPilot);
		this.elementPilot = elementPilot;
	}

	@Override
	protected WebElementPoller createPoller() {
		return new WebElementPoller(this);
	}

	public void click() {
		failUnless().clicked();
	}

	public void assertPresent() {
		failUnless().present();
	}

	public void expectMutations(Predicate<DomMutation> filter) {
		elementPilot.expectMutations(filter);
	}

}
