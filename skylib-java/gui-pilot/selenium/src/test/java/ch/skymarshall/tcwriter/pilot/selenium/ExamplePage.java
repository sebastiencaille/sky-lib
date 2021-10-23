package ch.skymarshall.tcwriter.pilot.selenium;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ExamplePage {

	@FindBy(id = "Proceed")
	public WebElement proceed;
	
	@FindBy(id = "OK")
	public WebElement ok;
	
	@FindBy(id = "NotExisting")
	public WebElement notExisting;
	
}