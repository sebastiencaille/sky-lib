package ch.scaille.tcwriter.it;

import java.util.function.Consumer;
import java.util.function.Function;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import ch.scaille.tcwriter.pilot.selenium.PagePilot;
import ch.scaille.tcwriter.pilot.selenium.SeleniumPilot;

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
	 * @param selector the context selector (drop down)
	 * @param selectorSelection the selection on the selector
	 * @param applier the button that applies the selection
	 */
	public record ContextSelector(WebElement selector, Consumer<WebElement> selectorSelection, WebElement applier) {
	}

	public static Function<MainPage, ContextSelector> dictionary(String name) {
		return mp -> new ContextSelector(mp.dictionarySelector, e -> new Select(e).selectByVisibleText(name),
				mp.dictionarySelect);
	}

	public static Function<MainPage, ContextSelector> currentTestCase() {
		return mp -> new ContextSelector(mp.testCaseSelector, e -> {
			// noop
		}, mp.testCaseSelect);
	}

	public void assertSelected(Function<MainPage, ContextSelector> selector) {
		on(() -> selector.apply(this).selector).applyOrFail(selector.apply(this).selectorSelection);
		on(() -> selector.apply(this).applier).click();
	}

}
