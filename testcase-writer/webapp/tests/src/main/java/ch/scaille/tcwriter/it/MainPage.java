package ch.scaille.tcwriter.it;

import static ch.scaille.tcwriter.pilot.selenium.ElementPilot.click;

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

	public record ContextSelector(WebElement selector, Consumer<WebElement> selectorApplier, WebElement applier) {
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

	public void select(Function<MainPage, ContextSelector> selector) {
		polling(() -> selector.apply(this).selector, selector.apply(this).selectorApplier).orFail();
		polling(() -> selector.apply(this).applier, click()).orFail();
	}

}
