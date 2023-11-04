package ch.scaille.tcwriter.it;

import static ch.scaille.tcwriter.pilot.selenium.ElementPilot.click;

import java.util.function.Consumer;

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
	
	public MainPage(SeleniumPilot pilot) {
		super(pilot);
	}

	public static Consumer<MainPage> dictionary(String name) {
		return mp -> {
			mp.wait(() -> mp.dictionarySelector, e -> new Select(e).selectByVisibleText(name));
			mp.wait(() -> mp.dictionarySelect, click());
		};
	}

	public void select(Consumer<MainPage> selector) {
		selector.accept(this);
	}

}
