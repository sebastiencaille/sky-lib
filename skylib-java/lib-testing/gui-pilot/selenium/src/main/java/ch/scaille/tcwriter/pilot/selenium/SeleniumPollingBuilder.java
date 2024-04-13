package ch.scaille.tcwriter.pilot.selenium;

import org.openqa.selenium.WebElement;

import ch.scaille.tcwriter.pilot.PollingBuilder;

public class SeleniumPollingBuilder extends PollingBuilder<ElementPilot, WebElement> {

	public SeleniumPollingBuilder(ElementPilot elementPilot) {
		super(elementPilot);
	}

	public ResultHandler<Boolean> isEnabled() {
		return withConfiguration(polling -> polling.withReportText("is enabled")).apply(WebElement::isEnabled);
	}

	public ResultHandler<Boolean> click() {
		return withConfiguration(polling -> polling.withReportText("clicked")).apply(WebElement::click);
	}

	public ResultHandler<Boolean> textEquals(String text) {
		return withConfiguration(polling -> polling.withReportText("text equals '" + text + "'")).
				satisfy(element -> element.getText().equals(text));
	}

}
