package ch.scaille.testing.bdd.selenium;

import org.openqa.selenium.WebDriver;

import ch.scaille.tcwriter.pilot.selenium.SeleniumPilot;

public class PageProvider {

	public final WebDriver driver; 
	
	public final ExamplePage examplePage;
	
	
	
	public PageProvider(SeleniumPilot pilot) {
		this.driver = pilot.getDriver();
		this.examplePage = new ExamplePage(pilot);
	}
	
	
	
}
