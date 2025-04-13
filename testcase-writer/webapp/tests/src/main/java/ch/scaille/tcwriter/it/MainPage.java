package ch.scaille.tcwriter.it;

import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import ch.scaille.testing.testpilot.selenium.PagePilot;
import ch.scaille.testing.testpilot.selenium.SeleniumPilot;
import ch.scaille.util.helpers.LambdaExt;

public class MainPage extends PagePilot {

	@FindBy(id = "dictionarySelector")
	public WebElement dictionarySelector;

	@FindBy(id = "dictionarySelect")
	public WebElement dictionarySelect;

	@FindBy(id = "testcaseSelector")
	public WebElement testCaseSelector;

	@FindBy(id = "testcaseSelect")
	public WebElement testCaseSelect;

	public MainPage(SeleniumPilot pilot) {
		super(pilot);
	}

	/**
	 * To select the application context
	 * 
	 * @param selector          the context selector (drop down)
	 * @param selectorSelection the selection on the selector
	 * @param button            the button that applies the selection
	 */
	public record ContextSelector(WebElement selector, Consumer<WebElement> selectorSelection, WebElement button) {
	}

	public static Function<MainPage, ContextSelector> dictionary(String name) {
		return mp -> new ContextSelector(mp.dictionarySelector, e -> new Select(e).selectByVisibleText(name),
				mp.dictionarySelect);
	}

	public static Function<MainPage, ContextSelector> testDictionary() {
		return dictionary("Test dictionary");
	}
	
	public static Function<MainPage, ContextSelector> currentTestCase() {
		return mp -> new ContextSelector(mp.testCaseSelector, _ -> LambdaExt.doNothing(), mp.testCaseSelect);
	}

	public void select(Function<MainPage, ContextSelector> selector) {
		on(() -> selector.apply(this).button).failUnless().clicked();
	}

	public void assertAvailable(Function<MainPage, ContextSelector> selector) {
		on(() -> selector.apply(this).selector).failUnless()
				.asserted(element -> Assertions.assertNotEquals("", element.getText(),  element + " has no selectable entry"));
	}

}
