package ch.scaille.testing.testpilot.selenium;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DomMutation;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ch.scaille.testing.testpilot.PolledComponent;
import ch.scaille.testing.testpilot.builder.AssertionBuilder;
import ch.scaille.testing.testpilot.builder.AssertionChain;
import ch.scaille.testing.testpilot.builder.AssertionResult;
import ch.scaille.testing.testpilot.builder.DefaultConfigurer;
import ch.scaille.testing.testpilot.factories.Pollings;
import ch.scaille.testing.testpilot.factories.Reporting;
import ch.scaille.util.helpers.JavaExt;


public class SeleniumAssertionBuilder extends
		AssertionBuilder<WebElement, SeleniumAssertionBuilder, SeleniumAssertionBuilder.WebElementAssertion, DefaultConfigurer<WebElement>> {

	public static Predicate<PolledComponent<WebElement>> satisfies(
			Function<WebElement, ExpectedCondition<WebElement>> expectedCondition) {
		return context -> expectedCondition.apply(context.component())
				.apply(context.getGuiPilot(SeleniumPilot.class).getDriver()) != null;
	}

	public static class WebElementAssertion extends AssertionChain<WebElement> {

		protected WebElementAssertion(AssertionBuilder<WebElement, ?, ?, ?> builder) {
			super(builder);
		}

		public AssertionResult<WebElement> present() {
			return testAssertion(Pollings.exists());
		}

		public AssertionResult<WebElement> isEnabled() {
			return configure(polling -> polling.reporting(Reporting.text("is enabled")))
					.satisfiedCtxt(satisfies(ExpectedConditions::elementToBeClickable));
		}
	}
	
	public static Consumer<PolledComponent<WebElement>> mutations(Predicate<List<DomMutation>> mutationsTest) {
		return ctxt -> mutationsTest.test(((WebElementPilot) ctxt.componentPilot()).getMutations());
	}
	
	public static Consumer<PolledComponent<WebElement>> assertMutations(Consumer<List<DomMutation>> mutationsTest) {
		return ctxt -> mutationsTest.accept(((WebElementPilot) ctxt.componentPilot()).getMutations());
	}

	private final WebElementPilot elementPilot;

	public SeleniumAssertionBuilder(WebElementPilot elementPilot) {
		super(elementPilot);
		this.elementPilot = elementPilot;
	}

	@Override
	protected WebElementAssertion createPoller() {
		return new WebElementAssertion(this);
	}

	public void assertPresent() {
		failUnless().present();
	}

	public JavaExt.AutoCloseableNoException expectMutations(Predicate<DomMutation> filter) {
		elementPilot.expectMutations(filter);
		return elementPilot::stopExpectingMutations;
	}

}
