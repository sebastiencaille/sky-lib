package ch.scaille.testing.bdd.selenium;

import org.openqa.selenium.WebDriver;

import ch.scaille.tcwriter.pilot.selenium.ExamplePage;
import ch.scaille.tcwriter.pilot.selenium.SeleniumPilot;
import ch.scaille.testing.bdd.definition.AbstractAppTestApi;

public class AppPages extends AbstractAppTestApi<AppPages.Context> {

	public static class Context {
		String example;
	}

	public final WebDriver driver;

	public final ExamplePage examplePage;

	public AppPages(SeleniumPilot pilot) {
		super(Context::new);
		this.driver = pilot.getDriver();
		this.examplePage = new ExamplePage(pilot);
	}

}
